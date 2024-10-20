package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Notification Exception</h3>
 * {@link RuntimeException} to indicate a message broker send error.
*/

///
public final class NotificationException extends RuntimeException {

    ///
    /**
     * Instantiates a new {@link NotificationException} object with the specified message.
     * @param message : The detail message
    */
    public NotificationException(String message) {

        super(message);
    }

    ///
}
