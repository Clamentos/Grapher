package io.github.clamentos.grapher.auth.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.clamentos.grapher.auth.business.services.UserService;
import io.github.clamentos.grapher.auth.web.dtos.UserDetails;
import io.github.clamentos.grapher.auth.web.dtos.UsernamePassword;

@RestController
@RequestMapping(

    path = "/grapher/user",
    consumes = "application/json",
    produces = "application/json"
)

public final class UserController {

    private final UserService service;

    public UserController(UserService service) {

        this.service = service;
    }

    @GetMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody UsernamePassword credentials) {

        return(ResponseEntity.ok(service.login(credentials)));
    }

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody UserDetails userDetails) {

        service.register(userDetails);
        return(ResponseEntity.ok().build());
    }

    // ...
}
