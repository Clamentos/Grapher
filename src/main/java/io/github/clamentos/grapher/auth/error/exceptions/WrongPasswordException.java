package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Wrong Password Exception</h3>
 * {@link AuthenticationException} to specifically indicate user login failures caused by wrong password.
 * This exception exists as a transaction rollback exclusion.
*/

///
public final class WrongPasswordException extends AuthenticationException {

    ///
    /**
     * Instantiates a new {@link WrongPasswordException} object with the specified message.
     * @param message : The detail message
    */
    public WrongPasswordException(String message) {

        super(message);
    }

    ///
}
