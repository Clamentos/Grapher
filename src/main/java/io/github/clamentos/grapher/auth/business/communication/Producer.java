package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;
import io.github.clamentos.grapher.auth.business.communication.events.ForgotPasswordEvent;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.amqp.AmqpException;

///..
import org.springframework.amqp.rabbit.core.RabbitTemplate;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Component;

///
/**
 * <h3>Producer</h3>
 * Spring {@link Component} dedicated to send auth responses to the other microservices.
*/

///
@Component
@Slf4j

///
public class Producer {

    ///
    private final RabbitTemplate template;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public Producer(RabbitTemplate template) {

        this.template = template;
    }

    ///
    /**
     * Sends the auth response to the specified destination.
     * @param destination : The target destination.
     * @param authResponse : The response message.
     * @apiNote If the response message cannot be delivered for any reason, it will simply be dropped.
    */
    public void respondToAuthRequest(String destination, AuthResponse authResponse) {

        try { template.convertAndSend(destination, "", authResponse); }
        catch(AmqpException exc) { log.error("Could not respond because: {}: {}", exc.getClass().getSimpleName(), exc.getMessage()); }
    }

    ///..
    /**
     * Sends the provided forgot password event to the message microservice.
     * @param event : The target event to send.
     * @throws AmqpException If any broker exception occurs.
    */
    public void sendForgotPasswordEvent(ForgotPasswordEvent event) throws AmqpException {

        template.convertAndSend("ForgotPasswordExchange", "", event);
    }

    ///
}
