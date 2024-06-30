package io.github.clamentos.grapher.auth.parameters.user;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum LoginInvalidDto {

    ///
    DTO_0("{\"username\":\"\",\"password\":\"password123\"}"),
    DTO_1("{\"username\":null,\"password\":\"password123\"}"),
    DTO_2("{\"username\":\"TestingUser\",\"password\":\"\"}"),
    DTO_3("{\"username\":\"TestingUser\",\"password\":null}");

    ///
    private final String dto;

    ///
}
