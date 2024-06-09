package io.github.clamentos.grapher.auth.exceptions;

public final class LoginFailedException extends SecurityException {

    public LoginFailedException(String message) {

        super(message);
    }
}
