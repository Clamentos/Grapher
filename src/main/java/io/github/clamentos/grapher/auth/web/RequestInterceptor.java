package io.github.clamentos.grapher.auth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.clamentos.grapher.auth.business.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class RequestInterceptor implements HandlerInterceptor {

    private final TokenService service;

    @Autowired
    public RequestInterceptor(TokenService service) {

        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(service.isValid(request.getHeader("Authorization")) == false) {

            // ...
        }

        return(true);
    }
}
