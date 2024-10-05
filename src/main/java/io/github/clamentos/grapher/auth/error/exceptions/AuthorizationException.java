package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Authorization Exception</h3>
 * {@link SecurityException} to specifically indicate user authorization failures.
*/

///
public class AuthorizationException extends SecurityException {

    ///
    /**
     * Instantiates a new {@link AuthorizationException} object with the specified message.
     * @param message : The detail message
    */
    public AuthorizationException(String message) {

        super(message);
    }

    ///
}
