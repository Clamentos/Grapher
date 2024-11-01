package io.github.clamentos.grapher.auth.configuration;

///
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

///..
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

///
/**
 * <h3>Broker Configuration</h3>
 * Spring {@link Configuration} to setup various message broker beans.
*/

///
@Configuration

///
public class BrokerConfiguration {

    ///
    /** Configures the Spring's AMQP message converter to use Jackson's JSON converter. */
    @Bean
    public MessageConverter messageConverterBean() {

        return(new Jackson2JsonMessageConverter("io.github.clamentos.grapher.auth"));
    }

    ///
}
