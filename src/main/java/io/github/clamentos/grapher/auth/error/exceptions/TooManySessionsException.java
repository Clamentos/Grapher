package io.github.clamentos.grapher.auth.error.exceptions;

///
public final class TooManySessionsException extends SecurityException {

    ///
    public TooManySessionsException(String message) {

        super(message);
    }

    ///..
    public TooManySessionsException(String message, Throwable cause) {

        super(message, cause);
    }

    ///
}
