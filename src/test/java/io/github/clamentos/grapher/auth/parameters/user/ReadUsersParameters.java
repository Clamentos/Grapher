package io.github.clamentos.grapher.auth.parameters.user;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum ReadUsersParameters {

    PARAMETERS_0("", "", "", "", "");
    //...

    private final String username;
    private final String email;
    private final String createdAtRange;
    private final String updatedAtRange;
    private final String operations;
}
