package io.github.clamentos.grapher.auth.web;

import io.github.clamentos.grapher.auth.business.services.SessionService;
///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///.
import java.util.Map;
import java.util.Set;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicLong;

///.
import org.springframework.dao.DataAccessException;

///..
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

///
/**
 * <h3>Request Interceptor</h3>
 * Custom {@link HandlerInterceptor} to perform request authentication and authorization.
*/

///
public class RequestInterceptor implements HandlerInterceptor {

    ///
    private final SessionService sessionService;

    ///..
    private final Set<String> authenticationExcludedPaths;
    private final Map<String, Set<UserRole>> authorizationMappings;

    ///..
    public static final Map<Integer, AtomicLong> requestStatusStatistics = new ConcurrentHashMap<>();
    public static final AtomicLong requests = new AtomicLong();

    ///
    /**
     * Instantiates a new {@link RequestInterceptor} object.
     * @param sessionService : The session service used to perform authentication and authorization.
     * @param authenticationExcludedPaths : The paths excluded from authentication.
     * @param authorizationMappings : The set of roles associated to each path used for authorization.
    */
    public RequestInterceptor(

        SessionService sessionService,
        Set<String> authenticationExcludedPaths,
        Map<String, Set<UserRole>> authorizationMappings
    ) {

        this.sessionService = sessionService;
        this.authenticationExcludedPaths = authenticationExcludedPaths;
        this.authorizationMappings = authorizationMappings;
    }

    ///
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws AuthenticationException, AuthorizationException, DataAccessException {

        requests.incrementAndGet();

        String uri = (String)request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();
        String key = uri + method;

        if(authenticationExcludedPaths.contains(key) == false) {

            String header = request.getHeader("Authorization");

            if(header != null) {

                request.setAttribute("session", sessionService.check(

                    header,
                    authorizationMappings.get(key),
                    null,
                    "Not enough privileges to call this URL"
                ));

                return(true);
            }

            throw new AuthenticationException(ErrorFactory.create(

                ErrorCode.INVALID_AUTH_HEADER,
                "RequestInterceptor::preHandle -> Bad or missing auth header"
            ));
        }

        else request.setAttribute("session", null);
        return(true);
	}

    ///..
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        requestStatusStatistics.computeIfAbsent(response.getStatus(), key -> new AtomicLong()).incrementAndGet();
	}

    ///
}
