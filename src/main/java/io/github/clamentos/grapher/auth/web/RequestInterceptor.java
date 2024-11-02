package io.github.clamentos.grapher.auth.web;

///
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;
import io.github.clamentos.grapher.auth.error.exceptions.ServiceUnavailableException;

///..
import io.github.clamentos.grapher.auth.monitoring.Readiness;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///.
import java.util.Map;
import java.util.Set;

///.
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

///
/**
 * <h3>Request Interceptor</h3>
 * Custom {@link HandlerInterceptor} to perform request authentication and authorization.
*/

///
public class RequestInterceptor implements HandlerInterceptor {

    ///
    private static final String SESSION = "session";

    ///..
    private final SessionService sessionService;
    private final Readiness readiness;

    ///..
    private final Set<String> authenticationExcludedPaths;
    private final Set<String> authenticationOptionalPaths;
    private final Map<String, Set<UserRole>> authorizationMappings;

    ///
    /**
     * Instantiates a new {@link RequestInterceptor} object.
     * @param sessionService : The session service used to perform authentication and authorization.
     * @param authenticationExcludedPaths : The paths excluded from authentication.
     * @param authorizationMappings : The set of roles associated to each path used for authorization.
    */
    public RequestInterceptor(

        SessionService sessionService,
        Readiness readiness,
        Set<String> authenticationExcludedPaths,
        Set<String> authenticationOptionalPaths,
        Map<String, Set<UserRole>> authorizationMappings
    ) {

        this.sessionService = sessionService;
        this.readiness = readiness;
        this.authenticationExcludedPaths = authenticationExcludedPaths;
        this.authenticationOptionalPaths = authenticationOptionalPaths;
        this.authorizationMappings = authorizationMappings;
    }

    ///
    /**
	 * Performs authentication and authorization.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and / or instance evaluation
	 * @return Always {@code true}.
	 * @throws AuthenticationException If authentication fails.
     * @throws AuthorizationException If authorization fails.
     * @throws ServiceUnavailableException If the service is currently unavailable.
	*/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws AuthenticationException, AuthorizationException, ServiceUnavailableException {

        if(!readiness.getIsReady().get()) {

            throw new ServiceUnavailableException(ErrorFactory.create(

                ErrorCode.SERVICE_TEMPORARILY_UNAVAILABLE,
                "Service is temporary unavailable",
                readiness.getDownDuration()
            ));
        }

        String uri = (String)request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String key = request.getMethod() + uri;
        String header = request.getHeader("Cookie");

        if(header != null && header.length() > 16 && header.startsWith("sessionIdCookie")) {

            header = header.substring(16);
        }

        if(authenticationOptionalPaths.contains(key)) {

            request.setAttribute(

                SESSION,
                header != null ? sessionService.check(header, Set.of(), null, "Interceptor optional path check failed") : null
            );
        }

        else if(!authenticationExcludedPaths.contains(key)) {

            if(header != null) {

                request.setAttribute(

                    SESSION,
                    sessionService.check(header, authorizationMappings.get(key), null, "Not enough privileges to call this URL")
                );

                return(true);
            }

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.INVALID_AUTH_HEADER,
                "RequestInterceptor::preHandle -> Bad or missing auth header"
            ));
        }

        else request.setAttribute(SESSION, null);
        return(true);
	}

    ///
}
