package io.github.clamentos.grapher.auth.messaging;

import java.util.List;
import java.util.Map;

///
import org.springframework.amqp.rabbit.core.RabbitTemplate;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Component;

import io.github.clamentos.grapher.auth.utility.Permission;
import io.github.clamentos.grapher.auth.utility.UserSession;

///
@Component

///
public class Publisher { // TODO: exception handling

    ///
    private final RabbitTemplate template;

    ///
    @Autowired
    public Publisher(RabbitTemplate template) {

        this.template = template;
    }

    ///
    public void publishSessions(Destination destination, Map<String, UserSession> sessions) {

        switch(destination) {

            case GRAPH_MS: template.convertAndSend("GraphMSReqExchange", "", sessions); break;
            case MEDIA_MS: template.convertAndSend("MediaMSReqExchange", "", sessions); break;
        }
    }

    public void publishSessionGenerated(String sessionId, UserSession session) {

        template.convertAndSend("ConsumeExchange", "", new ConsumeExchangeMessage(sessionId, session, null));
    }

    public void publishSessionRemoved(String sessionId) {

        template.convertAndSend("ConsumeExchange", "", new ConsumeExchangeMessage(sessionId, null, null));
    }

    public void publishReloadEvent(Map<String, List<Permission>> apiPermissions) {

        template.convertAndSend("ConsumeExchange", "", new ConsumeExchangeMessage(null, null, apiPermissions));
    }

    ///
}
