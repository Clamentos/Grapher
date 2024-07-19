package io.github.clamentos.grapher.auth.error.exceptions;

///
public final class SessionNotFoundException extends SecurityException {

    ///
    public SessionNotFoundException(String message) {

        super(message);
    }

    ///..
    public SessionNotFoundException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
