package io.github.clamentos.grapher.auth.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public final class LoginDetails {

    private final String username;
    private final String password;
}
