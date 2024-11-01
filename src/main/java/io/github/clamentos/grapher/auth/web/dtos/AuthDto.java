package io.github.clamentos.grapher.auth.web.dtos;

///
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

///.
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Auth Dto</h3>
 * Outgoing-only DTO for HTTP login responses.
*/

///
@JsonInclude(value = Include.NON_NULL)
@AllArgsConstructor
@Getter

///
public final class AuthDto {

    ///
    private final UserDto userDetails;
    private final String sessionId;
    private final long sessionExpiresAt;

    ///
}
