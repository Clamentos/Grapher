package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Authentication Exception</h3>
 * {@link SecurityException} to specifically indicate user authentication failures.
*/

///
public class AuthenticationException extends SecurityException {

    ///
    /**
     * Instantiates a new {@link AuthenticationException} object with the specified message.
     * @param message : The detail message
    */
    public AuthenticationException(String message) {

        super(message);
    }

    ///
}
