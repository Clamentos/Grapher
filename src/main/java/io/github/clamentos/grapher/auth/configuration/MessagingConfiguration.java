package io.github.clamentos.grapher.auth.configuration;

///
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

///.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

///
@Configuration

///
public class MessagingConfiguration {

    ///
    @Bean
    public MessageConverter messageConverterBean() {

        DefaultJackson2JavaTypeMapper classMapper = new DefaultJackson2JavaTypeMapper();
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();

        classMapper.setTrustedPackages("io.github.clamentos.grapher.auth.messaging");
        messageConverter.setClassMapper(classMapper);

        return(messageConverter);
    }

    ///
}
