package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Username Email Dto</h3>
 * Ingoing-only DTO for "forgot password" feature.
*/

///
@Getter
@Setter

///
public final class UsernameEmailDto {

    ///
    private String username;
    private String email;

    ///
}
