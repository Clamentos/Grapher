package io.github.clamentos.grapher.auth.business.communication.events;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import io.github.clamentos.grapher.auth.error.ErrorCode;

///..
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.List;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Auth Response</h3>
 * Outgoing-only DTO used as an authentication / authorization broker event response.
*/

///
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@Getter

///
public final class AuthResponse {

    ///
    private final long requestId;

    ///..
    private final ErrorCode errorCode;
    private final List<String> errorArguments;

    ///..
    private final long userId;
    private final String username;
    private final UserRole role;

    ///
}
