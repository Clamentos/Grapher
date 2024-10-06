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
 * Outgoing-only DTO used as an authentication / authorization response broker event.
*/

///
@AllArgsConstructor
@Getter

///
public final class AuthResponse {

    ///
    public static final transient String TYPE = "AuthResponse";

    ///.
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
