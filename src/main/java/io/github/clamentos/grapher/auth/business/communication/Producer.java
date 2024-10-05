package io.github.clamentos.grapher.auth.business.communication;

///
import io.github.clamentos.grapher.auth.business.communication.events.AuthResponse;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.amqp.AmqpException;

///..
import org.springframework.amqp.rabbit.core.RabbitTemplate;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.scheduling.annotation.Async;

///..
import org.springframework.stereotype.Component;

///
@Component
@Slf4j

///
public class Producer {

    ///
    private final RabbitTemplate template;

    ///
    @Autowired
    public Producer(RabbitTemplate template) {

        this.template = template;
    }

    ///
    @Async
    public void respondToAuthRequest(String destination, AuthResponse authResponse) {

        try { template.convertAndSend(destination, "", authResponse); }
        catch(AmqpException exc) { log.error("Could not respond because: {}: {}", exc.getClass().getSimpleName(), exc.getMessage()); }
    }

    ///
}
