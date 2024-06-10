package io.github.clamentos.grapher.auth.exceptions;

public final class AuthenticationException extends SecurityException {

    public AuthenticationException(String message) {

        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {

        super(message, cause);
    }
}
