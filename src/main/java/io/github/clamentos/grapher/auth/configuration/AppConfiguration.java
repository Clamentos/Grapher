package io.github.clamentos.grapher.auth.configuration;

///..
import io.github.clamentos.grapher.auth.business.communication.Producer;

///..
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.utility.BeanProvider;

///..
import io.github.clamentos.grapher.auth.web.RequestInterceptor;

///.
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.function.Consumer;

///.
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

///..
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.boot.LazyInitializationExcludeFilter;

///..
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

///..
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

///..
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

///
/**
 * <h3>App Configuration</h3>
 * Spring {@link Configuration} to setup various configuration beans.
*/

///
@Configuration
@EnableWebMvc
@EnableAsync
@EnableScheduling

///
public class AppConfiguration implements WebMvcConfigurer {

    ///
    private final SessionService sessionService;

    ///..
    private final Set<String> authenticationExcludedPaths;
    private final Set<String> authenticationOptionalPaths;
    private final Map<String, Set<UserRole>> authorizationMappings;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public AppConfiguration(SessionService sessionService) {

        this.sessionService = sessionService;

        authenticationExcludedPaths = new HashSet<>();

        authenticationExcludedPaths.add("POST/grapher/v1/auth-service/user/login");
        authenticationExcludedPaths.add("GET/grapher/v1/auth-service/observability");

        authenticationOptionalPaths = new HashSet<>();
        authenticationOptionalPaths.add("POST/grapher/v1/auth-service/user/register");

        Set<UserRole> admin = Set.of(UserRole.ADMINISTRATOR);
        Set<UserRole> privileged = Set.of(UserRole.ADMINISTRATOR, UserRole.MODERATOR);

        authorizationMappings = new ConcurrentHashMap<>();

        authorizationMappings.put("GET/grapher/v1/auth-service/observability/status", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/audits", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/logs", admin);
        authorizationMappings.put("DELETE/grapher/v1/auth-service/observability/audits", admin);
        authorizationMappings.put("DELETE/grapher/v1/auth-service/observability/logs", admin);

        authorizationMappings.put("POST/grapher/v1/auth-service/user/subscriptions", Set.of());
        authorizationMappings.put("PATCH/grapher/v1/auth-service/user/subscriptions", Set.of());
        authorizationMappings.put("DELETE/grapher/v1/auth-service/user/subscriptions", Set.of());

        authorizationMappings.put("DELETE/grapher/v1/auth-service/user/logout", Set.of());
        authorizationMappings.put("DELETE/grapher/v1/auth-service/user/logout/all", Set.of());
        authorizationMappings.put("GET/grapher/v1/auth-service/user/search", privileged);
        authorizationMappings.put("GET/grapher/v1/auth-service/user/{id}", Set.of());
        authorizationMappings.put("PATCH/grapher/v1/auth-service/user", Set.of());
        authorizationMappings.put("DELETE/grapher/v1/auth-service/user/{id}", Set.of());
    }

    ///
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

		registry

            .addInterceptor(new RequestInterceptor(

                sessionService,
                authenticationExcludedPaths,
                authenticationOptionalPaths,
                authorizationMappings)
            )
            .addPathPatterns("/**")
        ;
	}

    ///..
    @Bean
    public static LazyInitializationExcludeFilter lazyInitExcludeFilter() {

        return(LazyInitializationExcludeFilter.forBeanTypes(BeanProvider.class, Producer.class, Consumer.class));
    }

    ///..
    @Bean
    public MessageConverter messageConverterBean() {

        return(new Jackson2JsonMessageConverter("io.github.clamentos.grapher.auth"));
    }

    ///
}
