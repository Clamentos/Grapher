package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthRequest;
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;

///..
import io.github.clamentos.grapher.auth.business.services.SessionService;
import io.github.clamentos.grapher.auth.business.services.ValidatorService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///.
import java.util.List;

///.
import org.springframework.amqp.rabbit.annotation.RabbitListener;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Component;

///
/**
 * <h3>Consumer</h3>
 * Spring {@link Component} dedicated to handle AMQP auth requests from the other microservices.
*/

///
@Component

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
    /**
     * Handles auth requests from the {@code Graph} microservice.
     * @param authRequest : The request to handle.
     * @apiNote This method will subsequently call the {@link Consumer} in order to send the response.
    */
    @RabbitListener(queues = "GraphMsToAuthMsQueue")
    public void processFromGraphMs(AuthRequest authRequest) {

        this.processAndRespond(authRequest, "AuthMsToGraphMsQueue");
    }

    ///..
    /**
     * Handles auth requests from the {@code Message} microservice.
     * @param authRequest : The request to handle.
     * @apiNote This method will subsequently call the {@link Consumer} in order to send the response.
    */
    @RabbitListener(queues = "MessageMsToAuthMsQueue")
    public void processFromMessageMs(AuthRequest authRequest) {

        this.processAndRespond(authRequest, "AuthMsToMessageMsQueue");
    }

    ///.
    private void processAndRespond(AuthRequest authRequest, String destination) throws NullPointerException {

        Session session = null;

        try {

            validatorService.requireNotNull(authRequest.getSessionId(), "sessionId");
            validatorService.requireNotNull(authRequest.getRequiredRoles(), "requiredRoles");

            session = sessionService.check(

                authRequest.getSessionId(),
                authRequest.getRequiredRoles(),
                authRequest.getMinimumRequiredRole(),
                "Check failed for source: " + destination
            );
        }

        catch(AuthenticationException | AuthorizationException | IllegalArgumentException exc) {

            producer.respondToAuthRequest(destination, this.generateResponse(authRequest.getRequestId(), exc, session));
            return;
        }

        producer.respondToAuthRequest(destination, this.generateResponse(authRequest.getRequestId(), null, session));
    }

    ///..
    private AuthResponse generateResponse(String requestId, Throwable exception, Session session) {

        long userId = session != null ? session.getUserId() : -1L;
        String username = session != null ? session.getUsername() : null;
        UserRole userRole = session != null ? session.getUserRole() : null;

        if(exception == null) return(new AuthResponse(requestId, null, List.of(), userId, username, userRole));

        String[] splits = exception.getMessage().split("/");
        ErrorCode errorCode = null;
        List<String> errorArguments = List.of();

        if(splits.length > 1) {

            errorCode = ErrorCode.valueOf(splits[0]);

            if(splits.length > 2) {

                for(int i = 2; i < splits.length; i++) {

                    errorArguments.add(splits[i]);
                }
            }
        }

        return(new AuthResponse(requestId, errorCode, errorArguments, userId, username, userRole));
    }

    ///
}
