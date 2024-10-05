package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthRequest;
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

///.
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

///..
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

///
/**
 * <h3>Custom Message Converter</h3>
 * Custom AMQP {@link MessageConverter} implementation specific for:
 * 
 * <ul>
 *   <li>{@link AuthRequest} as the incoming message.</li>
 *   <li>{@link AuthResponse} as the outgoing message.</li>
 * </ul>
*/

///
public final class CustomMessageConverter implements MessageConverter {

    ///
    /**
     * {@inheritDoc}
     * @throws MessageConversionException If any field of the message is {@code null},
     * or if {@code object} is not of type {@code AuthResponse}.
    */
    @Override // FIXME: must be able to accept nulls
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {

        // byte 0 -> "errorCode" length
        // byte 1 -> "errorParameters" length
        // byte 2 -> "username" length

        // bytes... -> errorCode
        // bytes... -> errorParameters (each is 1 byte for length + actual string data)
        // bytes... -> userId
        // bytes... -> username
        // bytes... -> role

        if(object instanceof AuthResponse) {

            AuthResponse response = (AuthResponse)object;

            if(
                response.getErrorCode() == null ||
                response.getErrorArguments() == null ||
                response.getUsername() == null ||
                response.getRole() == null ||
                response.getRequestId() == null
            ) {

                throw new MessageConversionException("Illegal object field values");
            }

            byte[] rawErrorCode = response.getErrorCode().getBytes();
            List<byte[]> rawErrorArguments = new ArrayList<>(response.getErrorArguments().size());

            for(String errorArgument : response.getErrorArguments()) {

                if(errorArgument == null) throw new MessageConversionException("Illegal object field values");
                rawErrorArguments.add(errorArgument.getBytes());
            }

            byte[] rawUserId = new byte[8];
            rawUserId[0] = (byte)(response.getUserId() & 0x00000001);
            rawUserId[1] = (byte)(response.getUserId() & 0x00000002);
            rawUserId[2] = (byte)(response.getUserId() & 0x00000004);
            rawUserId[3] = (byte)(response.getUserId() & 0x00000008);
            rawUserId[4] = (byte)(response.getUserId() & 0x00000010);
            rawUserId[5] = (byte)(response.getUserId() & 0x00000020);
            rawUserId[6] = (byte)(response.getUserId() & 0x00000040);
            rawUserId[7] = (byte)(response.getUserId() & 0x00000080);

            byte[] rawUsername = response.getUsername().getBytes();
            byte rawRole = (byte)response.getRole().ordinal();

            int dataLength = 12 + rawErrorCode.length + (rawErrorArguments.size() * 2) + rawUsername.length;

            int index = 0;
            byte[] data = new byte[dataLength];

            data[index++] = (byte)rawErrorCode.length;
            data[index++] = (byte)rawErrorArguments.size();
            data[index++] = (byte)rawUsername.length;

            index = fillArray(data, rawErrorCode, index);

            for(byte[] bytes : rawErrorArguments) {

                data[index++] = (byte)bytes.length;
                index = fillArray(data, bytes, index);
            }

            index = fillArray(data, rawUserId, index);
            index = fillArray(data, rawUsername, index);
            data[index] = rawRole;

            messageProperties.setContentType(AuthResponse.TYPE);
            messageProperties.setCorrelationId(response.getRequestId());

            return(new Message(data, messageProperties));
        }

        else throw new MessageConversionException("Unknown or illegal message class: '" + object.getClass().getSimpleName() + "'");
    }

    ///..
    /**
     * {@inheritDoc}
     * @throws MessageConversionException If {@link Message#body} is {@code null} or less than 35 bytes,
     * if the message correlation id is {@code null} or if any of the {@link UserRole} in the message are invalid ordinals.
    */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {

        String type = message.getMessageProperties().getContentType();

        if(AuthRequest.TYPE.equals(type)) return(deserialize(message.getBody(), message.getMessageProperties().getCorrelationId()));
        else throw new MessageConversionException("Unknown or illegal message type: '" + type + "'");
    }

    ///.
    private int fillArray(byte[] toFill, byte[] data, int start) {

        int i = 0;

        while(i < data.length) toFill[start + i] = data[i++];
        return(start + i);
    }

    ///..
    private AuthRequest deserialize(byte[] data, String correlationId) throws MessageConversionException {

        // data[0] -> length of "sessionId" string field
        // data[1] -> length of "roles" array field
        // data[2...X] -> sessionId
        // data[X+1] -> minimum required role
        // ... roles (a single byte for each enum ordinal)

        if(data == null || data.length < 35) throw new MessageConversionException("Payload cannot be null or less than 35 bytes long");
        if(correlationId == null) throw new MessageConversionException("Correlation id cannot be null");

        try {

            String sessionId = new String(data, 2, 0x000000FF & data[0]);
            byte rawMinimumRequiredRole = data[sessionId.length() + 2];
            Set<UserRole> requiredRoles = new HashSet<>();
            int numRequiredRoles = 0x000000FF & data[1];

            for(int i = 0; i < numRequiredRoles; i++) {

                requiredRoles.add(UserRole.decode(data[sessionId.length() + 3 + i]));
            }

            return(new AuthRequest(

                correlationId,
                sessionId,
                rawMinimumRequiredRole >= 0 ? UserRole.decode(rawMinimumRequiredRole) : null,
                requiredRoles
            ));
        }

        catch(RuntimeException exc) {

            throw new MessageConversionException("Could not deserialize message", exc);
        }
    }

    ///
}
