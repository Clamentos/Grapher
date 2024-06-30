package io.github.clamentos.grapher.auth.parameters.user;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum RegisterInvalidDto {

    ///
    DTO_0("{\"username\":\"\",\"password\":\"password123\",\"email\":\"testinguser2@nonexistent.com\"}"),
    DTO_1("{\"username\":\"TestingUser2\",\"password\":null,\"email\":\"testinguser2@nonexistent.com\"}");
    //...

    ///
    private final String dto;

    ///
}
