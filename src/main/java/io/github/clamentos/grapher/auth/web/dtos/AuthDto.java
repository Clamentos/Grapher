package io.github.clamentos.grapher.auth.web.dtos;

///
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public final class AuthDto {

    ///
    private UserDto userDetails;
    private String token;

    ///
}
