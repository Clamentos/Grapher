package io.github.clamentos.grapher.auth.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component // TODO:
public class Subscriber {

    public Subscriber() {

        System.out.println("instantiated");
    }

    /* @RabbitListener(queues = "mediaMsQueue")
    public void receiveMessageFromFanout1(BlacklistMessage message) {

        System.out.println("Received message from mediaMsQueue: " + message);
    }

    @RabbitListener(queues = "graphMsQueue")
    public void receiveMessageFromFanout2(BlacklistMessage message) {

        System.out.println("Received message from graphMsQueue: " + message);
    } */
}
