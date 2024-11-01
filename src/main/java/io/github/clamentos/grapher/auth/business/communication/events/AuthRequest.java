package io.github.clamentos.grapher.auth.business.communication.events;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.Set;

///.
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Auth Request</h3>
 * Incoming-only DTO used as an authentication / authorization request broker event.
*/

///
@Getter
@Setter

///
public final class AuthRequest {

    ///
    private long requestId;

    ///..
    private String sessionId;
    private UserRole minimumRequiredRole;
    private Set<UserRole> requiredRoles;

    ///
}
