package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Illegal Action Exception</h3>
 * {@link RuntimeException} to specifically indicate actions that no user, regardless of role, can perform.
*/

///
public final class IllegalActionException extends AuthorizationException {

    ///
    /**
     * Instantiates a new {@link IllegalActionException} object with the specified message.
     * @param message : The detail message
    */
    public IllegalActionException(String message) {

        super(message);
    }

    ///
}
