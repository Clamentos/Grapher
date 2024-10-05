package io.github.clamentos.grapher.auth.business.communication.events;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.Set;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Auth Request</h3>
 * DTO used as an authentication / authorization request broker event.
*/

///
@AllArgsConstructor
@Getter

///
public final class AuthRequest {

    ///
    public static final String TYPE = "AuthRequest";

    ///.
    /** This field is the AMQP {@code MessageProperties.correlationId}. It's only here to serve as a temporary hook. */
    private final String requestId;

    ///..
    private final String sessionId;
    private final UserRole minimumRequiredRole;
    private final Set<UserRole> requiredRoles;

    ///
}
