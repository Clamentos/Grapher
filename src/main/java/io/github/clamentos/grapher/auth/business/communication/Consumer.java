package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthRequest;
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;

///..
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///.
import java.util.ArrayList;
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

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public Consumer(Producer producer, SessionService sessionService) {

        this.producer = producer;
        this.sessionService = sessionService;
    }

    ///
    /**
     * Handles auth requests from the {@code Graph} microservice.
     * @param authRequest : The request to handle.
     * @apiNote This method will subsequently call the {@link Consumer} in order to send the response.
    */
    @RabbitListener(queues = "GraphMsToAuthMsQueue")
    public void processFromGraphMs(AuthRequest authRequest) {

        processAndRespond(authRequest, "AuthMsToGraphMsQueue");
    }

    ///..
    /**
     * Handles auth requests from the {@code Message} microservice.
     * @param authRequest : The request to handle.
     * @apiNote This method will subsequently call the {@link Consumer} in order to send the response.
    */
    @RabbitListener(queues = "MessageMsToAuthMsQueue")
    public void processFromMessageMs(AuthRequest authRequest) {

        processAndRespond(authRequest, "AuthMsToMessageMsQueue");
    }

    ///.
    private void processAndRespond(AuthRequest authRequest, String destination) throws NullPointerException {

        Session session = null;
        ErrorCode errorCode = null;
        List<String> errorArguments = new ArrayList<>();

        try {

            if(authRequest.getSessionId() != null && authRequest.getRequiredRoles() != null) {

                session = sessionService.check(

                    authRequest.getSessionId(),
                    authRequest.getRequiredRoles(),
                    authRequest.getMinimumRequiredRole(),
                    "Check failed for source: " + destination
                );   
            }

            else errorCode = ErrorCode.VALIDATOR_REQUIRE_NOT_NULL;
        }

        catch(AuthenticationException | AuthorizationException exc) {

            String[] splits = exc.getMessage().split("/");

            if(splits.length > 1) {

                errorCode = ErrorCode.valueOf(splits[0]);

                if(splits.length > 2) {

                    for(int i = 2; i < splits.length; i++) {

                        errorArguments.add(splits[i]);
                    }
                }
            }
        }

        producer.respondToAuthRequest(destination, new AuthResponse(

            authRequest.getRequestId(),
            errorCode,
            errorArguments,
            session != null ? session.getUserId() : -1,
            session != null ? session.getUsername() : null,
            session != null ? session.getUserRole() : null
        ));
    }

    ///
}
