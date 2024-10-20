package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.Getter;
import lombok.Setter;

///
/**
 * <h3>Forgot Password Dto</h3>
 * Ingoing-only DTO for "forgot password" feature.
*/

///
@Getter
@Setter

///
public final class ForgotPasswordDto {

    ///
    private String forgotPasswordSessionId;
    private String password;

    ///
}
