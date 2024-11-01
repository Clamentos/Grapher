package io.github.clamentos.grapher.auth.configuration;

///
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.monitoring.Readiness;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.web.RequestInterceptor;

///.
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

///..
import org.springframework.core.task.SimpleAsyncTaskExecutor;

///..
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

///..
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

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
    private final Readiness readiness;

    ///..
    private final Set<String> authenticationExcludedPaths;
    private final Set<String> authenticationOptionalPaths;
    private final Map<String, Set<UserRole>> authorizationMappings;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public AppConfiguration(SessionService sessionService, Readiness readiness) {

        this.sessionService = sessionService;
        this.readiness = readiness;

        authenticationExcludedPaths = new HashSet<>();

        authenticationExcludedPaths.add("POST/grapher/v1/auth-service/user/login");
        authenticationExcludedPaths.add("GET/grapher/v1/auth-service/observability");
        authenticationExcludedPaths.add("GET/grapher/v1/auth-service/user/forgot-password");
        authenticationExcludedPaths.add("POST/grapher/v1/auth-service/user/forgot-password");

        authenticationOptionalPaths = new HashSet<>();
        authenticationOptionalPaths.add("POST/grapher/v1/auth-service/user/register");

        Set<UserRole> admin = Set.of(UserRole.ADMINISTRATOR);
        Set<UserRole> privileged = Set.of(UserRole.ADMINISTRATOR, UserRole.MODERATOR);

        authorizationMappings = new ConcurrentHashMap<>();

        authorizationMappings.put("GET/grapher/v1/auth-service/observability/status", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/audits/count", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/audits", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/logs/count", admin);
        authorizationMappings.put("GET/grapher/v1/auth-service/observability/logs", admin);
        authorizationMappings.put("PATCH/grapher/v1/auth-service/observability/ready", admin);
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
                readiness,
                authenticationExcludedPaths,
                authenticationOptionalPaths,
                authorizationMappings)
            )
            .addPathPatterns("/**")
        ;
	}

    ///..
    /**
     * Configures the Spring's internal task scheduler to be a {@link SimpleAsyncTaskScheduler} with virtual threads support.
     * <ul>
     *   <li>The scheduler spawns 1 virtual thread per task.</li>
     *   <li>The scheduler generates the following task name prefix: {@code GrapherAuthScheduledTask-}</li>
     * </ul>
    */
    @Bean
    public SimpleAsyncTaskScheduler taskScheduler() {

        SimpleAsyncTaskScheduler scheduler = new SimpleAsyncTaskScheduler();

        scheduler.setTargetTaskExecutor(new SimpleAsyncTaskExecutor("GrapherAuthScheduledTask-"));
        scheduler.setVirtualThreads(true);

        return(scheduler);
    }

    ///
}
