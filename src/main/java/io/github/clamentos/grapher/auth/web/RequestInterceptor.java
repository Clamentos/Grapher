package io.github.clamentos.grapher.auth.web;

///
import io.github.clamentos.grapher.auth.business.services.TokenService;

///..
import io.github.clamentos.grapher.auth.exceptions.AuthenticationException;

///.
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.web.servlet.HandlerInterceptor;

///
public final class RequestInterceptor implements HandlerInterceptor {

    ///
    private final TokenService service;

    ///
    @Autowired
    public RequestInterceptor(TokenService service) {

        this.service = service;
    }

    ///
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws AuthenticationException {

        service.verifyToken(request.getHeader("Authorization"));
        return(true);
    }

    ///
}
