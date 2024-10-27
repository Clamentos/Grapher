package io.github.clamentos.grapher.auth.error.exceptions;

///
/**
 * <h3>Service Unavailable Exception</h3>
 * {@link RuntimeException} to indicate temporary service unavailability.
*/

///
public final class ServiceUnavailableException extends RuntimeException {

    ///
    /**
     * Instantiates a new {@link ServiceUnavailableException} object with the specified message.
     * @param message : The detail message
    */
    public ServiceUnavailableException(String message) {

        super(message);
    }

    ///
}
