package io.github.clamentos.grapher.auth.parameters.user;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum UpdateUserInvalidDto {

    ///
    DTO_0("{\"id\":null,\"email\":\"testinguserpawnmod@nonexistent.com\"}");

    ///
    private final String dto;

    ///
}
