package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthRequest;
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;

///..
import io.github.clamentos.grapher.auth.business.services.SessionService;
import io.github.clamentos.grapher.auth.business.services.ValidatorService;

///..
import io.github.clamentos.grapher.auth.error.ErrorDecoder;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.amqp.rabbit.annotation.RabbitListener;

///..
import org.springframework.amqp.support.AmqpHeaders;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.messaging.handler.annotation.Header;

///..
import org.springframework.stereotype.Component;

///
/**
 * <h3>Consumer</h3>
 * Spring {@link Component} dedicated to handle auth request events from the other microservices via the message broker.
*/

///
@Component
@Slf4j

///
public class Consumer {

    ///
    private final Producer producer;
    private final SessionService sessionService;
    private final ValidatorService validatorService;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public Consumer(Producer producer, SessionService sessionService, ValidatorService validatorService) {

        this.producer = producer;
        this.sessionService = sessionService;
        this.validatorService = validatorService;
    }

    ///
    /** Internal use only. {@link RabbitListener} on {@code MessageMsToAuthMsQueue} and {@code GraphMsToAuthMsQueue} queues. */
    @RabbitListener(queues = {"MessageMsToAuthMsQueue", "GraphMsToAuthMsQueue"})
    protected void consume(AuthRequest authRequest, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {

        switch(queue) {

            case "MessageMsToAuthMsQueue": this.processAndRespond(authRequest, "AuthMsToMessageMsQueue"); break;
            case "GraphMsToAuthMsQueue": this.processAndRespond(authRequest, "AuthMsToGraphMsQueue"); break;

            default: log.warn("Unknown queue: {}", queue);
        }
    }

    ///.
    private void processAndRespond(AuthRequest authRequest, String destination) throws NullPointerException {

        try {

            validatorService.validateAuthRequestEvent(authRequest);
        }

        catch(IllegalArgumentException exc) {

            producer.respondToAuthRequest(destination, this.generateResponse(authRequest.getRequestId(), exc, null));
            return;
        }

        try {

            Session session = sessionService.check(

                authRequest.getSessionId(),
                authRequest.getRequiredRoles(),
                authRequest.getMinimumRequiredRole(),
                "SessionService::check failed for source: " +
                destination + ", requestId: " + authRequest.getRequestId()
            );

            producer.respondToAuthRequest(destination, this.generateResponse(authRequest.getRequestId(), null, session));
        }

        catch(AuthenticationException | AuthorizationException exc) {

            producer.respondToAuthRequest(destination, this.generateResponse(authRequest.getRequestId(), exc, null));
        }
    }

    ///..
    private AuthResponse generateResponse(long requestId, Throwable exception, Session session) {

        if(exception == null) {

            if(session != null) {

                return(new AuthResponse(requestId, null, null, session.getUserId(), session.getUsername(), session.getUserRole()));
            }

            return(new AuthResponse(requestId, null, null, -1L, null, null));
        }

        ErrorDecoder decoder = new ErrorDecoder(exception);

        if(session != null) {

            return(new AuthResponse(

                requestId,
                decoder.getErrorCode(),
                decoder.getErrorArguments(),
                session.getUserId(),
                session.getUsername(),
                session.getUserRole()
            ));
        }

        return(new AuthResponse(requestId, decoder.getErrorCode(), decoder.getErrorArguments(), -1L, null, null));
    }

    ///
}
