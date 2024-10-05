package io.github.clamentos.grapher.auth.configuration;

///..
import io.github.clamentos.grapher.auth.business.communication.CustomMessageConverter;
import io.github.clamentos.grapher.auth.business.communication.Producer;
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
import java.util.function.Consumer;

///.
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
    private final Map<String, Set<UserRole>> authorizationMappings;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public AppConfiguration(SessionService sessionService) { // TODO: insert all the auth mappings

        this.sessionService = sessionService;

        authenticationExcludedPaths = new HashSet<>();
        authenticationExcludedPaths.add("POST/v1/grapher/user/register");
        authenticationExcludedPaths.add("POST/v1/grapher/user/login");

        authorizationMappings = new ConcurrentHashMap<>();
        authorizationMappings.put("GET/v1/grapher/auth/observability/status", Set.of(UserRole.ADMINISTRATOR));
        authorizationMappings.put("DELETE/v1/grapher/user/logout", Set.of());
        authorizationMappings.put("DELETE/v1/grapher/user/logout/all", Set.of());
        authorizationMappings.put("GET/v1/grapher/user", Set.of(UserRole.ADMINISTRATOR, UserRole.MODERATOR));
        authorizationMappings.put("GET/v1/grapher/user/{id}", Set.of());
        authorizationMappings.put("PATCH/v1/grapher/user", Set.of());
        authorizationMappings.put("POST/v1/grapher/user/{id}/subscriptions", Set.of());
        authorizationMappings.put("PATCH/v1/grapher/user/{id}/subscriptions", Set.of());
        authorizationMappings.put("PATCH/v1/grapher/user/{id}/notifications", Set.of());
        authorizationMappings.put("DELETE/v1/grapher/user/{id}", Set.of());
        authorizationMappings.put("DELETE/v1/grapher/user/{id}/subscriptions", Set.of());
        authorizationMappings.put("DELETE/v1/grapher/user/{id}/notifications", Set.of());
    }

    ///
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

		registry

            .addInterceptor(new RequestInterceptor(sessionService, authenticationExcludedPaths, authorizationMappings))
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

        return(new CustomMessageConverter());
    }

    ///
}
