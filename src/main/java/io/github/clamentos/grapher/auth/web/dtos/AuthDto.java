package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Auth Dto</h3>
 * Outgoing-only DTO for HTTP login responses.
*/

///
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
