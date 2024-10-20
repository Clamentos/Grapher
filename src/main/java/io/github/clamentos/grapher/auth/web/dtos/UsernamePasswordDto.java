package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Username Password Dto</h3>
 * Ingoing-only DTO for user login HTTP requests.
*/

///
@Getter
@Setter

///
public final class UsernamePasswordDto {

    ///
    private String username;
    private String password;

    ///
}
