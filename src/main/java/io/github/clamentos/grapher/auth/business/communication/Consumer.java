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
@Component

///
public class Consumer {

    ///
    private final Producer producer;
    private final SessionService sessionService;

    ///
    @Autowired
    public Consumer(Producer producer, SessionService sessionService) {

        this.producer = producer;
        this.sessionService = sessionService;
    }

    ///
    @RabbitListener(queues = "GraphMsToAuthMsQueue")
    public void processFromGraphMs(AuthRequest authRequest) {

        processAndRespond(authRequest, "AuthMsToGraphMsQueue");
    }

    ///..
    @RabbitListener(queues = "MessageMsToAuthMsQueue")
    public void processFromMessageMs(AuthRequest authRequest) {

        processAndRespond(authRequest, "AuthMsToMessageMsQueue");
    }

    ///.
    private void processAndRespond(AuthRequest authRequest, String destination) {

        Session session = null;
        String errorCode = null;
        List<String> errorArguments = new ArrayList<>();

        try {

            if(authRequest.getSessionId() != null && authRequest.getRequiredRoles() != null) {

                session = sessionService.check(

                    authRequest.getSessionId(),
                    authRequest.getRequiredRoles(),
                    authRequest.getMinimumRequiredRole(),
                    "Consumer::processAndRespond -> Check failed for source: " + destination
                );   
            }

            else errorCode = ErrorCode.VALIDATOR_REQUIRE_NOT_NULL.getValue();
        }

        catch(AuthenticationException | AuthorizationException exc) {

            String[] splits = exc.getMessage().split("\1");
            errorCode = splits[0];

            if(splits.length > 2) {

                for(int i = 2; i < splits.length; i++) {

                    errorArguments.add(splits[i]);
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
