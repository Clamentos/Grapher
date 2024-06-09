package io.github.clamentos.grapher.auth.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.clamentos.grapher.auth.business.services.UserService;
import io.github.clamentos.grapher.auth.web.dtos.LoginDetails;

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
    public ResponseEntity<String> login(@RequestBody LoginDetails details) {

        return(ResponseEntity.ok(service.login(details)));
    }

    // register(UserDto) -> void
    // ...
}
