package io.github.clamentos.grapher.auth.error.exceptions;

///
public final class AuthorizationException extends SecurityException {

    ///
    public AuthorizationException(String message) {

        super(message);
    }

    ///..
    public AuthorizationException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
