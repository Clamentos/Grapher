package io.github.clamentos.grapher.auth.web.interceptors;

///
import io.github.clamentos.grapher.auth.business.services.TokenService;

///..
import io.github.clamentos.grapher.auth.error.ErrorCode;
import io.github.clamentos.grapher.auth.error.ErrorFactory;

///..
import io.github.clamentos.grapher.auth.error.exceptions.AuthenticationException;
import io.github.clamentos.grapher.auth.error.exceptions.AuthorizationException;

///.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///.
import java.util.HashSet;
import java.util.Set;

///.
import org.springframework.web.servlet.HandlerInterceptor;

///
public class RequestInterceptor implements HandlerInterceptor {

    ///
    private final TokenService service;

    ///..
    private final Set<String> authenticationExcludedPaths;
    private final Set<String> authorizationExcludedPaths;

    ///
    public RequestInterceptor(TokenService service) {

        this.service = service;

        authenticationExcludedPaths = new HashSet<>();
        authorizationExcludedPaths = new HashSet<>();

        authenticationExcludedPaths.add("/v1/grapher/user/login");
        authenticationExcludedPaths.add("/v1/grapher/user/register");
        authenticationExcludedPaths.add("/v1/grapher/auth/observability");

        authorizationExcludedPaths.add("/v1/grapher/user/logout");
    }

    ///
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    throws AuthenticationException, AuthorizationException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if(authenticationExcludedPaths.contains(uri) == false) {

            String header = request.getHeader("Authorization");

            if(header != null && header.length() > 7) {

                String[] splits = header.split(" ");

                if(splits.length == 2) {

                    if(splits[0].equals("Bearer")) {

                        service.authenticate(splits[1]);

                        if(authorizationExcludedPaths.contains(uri) == false) {

                            if(Character.isDigit(uri.charAt(uri.length() - 1))) {

                                int lastSlashIndex = uri.lastIndexOf("/");
                                uri = uri.substring(0, lastSlashIndex + 1) + "{id}";
                            }

                            request.setAttribute("authChecks", service.authorize(splits[1], method + uri));
                        }

                        request.setAttribute("jwtToken", splits[1]);
                        return(true);
                    }
                }
            }

            throw new AuthenticationException(ErrorFactory.generate(ErrorCode.INVALID_TOKEN));
        }

		return(true);
	}

    ///
}
