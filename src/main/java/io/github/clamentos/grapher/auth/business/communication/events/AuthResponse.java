package io.github.clamentos.grapher.auth.business.communication.events;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.List;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Auth Response</h3>
 * DTO used as an authentication / authorization response broker event.
*/

///
@AllArgsConstructor
@Getter

///
public final class AuthResponse {

    ///
    public static final String TYPE = "AuthResponse";

    ///.
    /** This field is the AMQP {@code MessageProperties.correlationId}. It's only here to serve as a temporary hook. */
    private final String requestId;

    ///..
    private final String errorCode;
    private final List<String> errorArguments;

    ///..
    private final long userId;
    private final String username;
    private final UserRole role;

    ///
}
