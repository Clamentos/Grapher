package io.github.clamentos.grapher.auth.utility;

///
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor
@Getter

///
public enum ErrorCode {

    ///
    ENTITY_NOT_FOUND("EC000"),
    ENTITY_ALREADY_EXISTS("EC001"),
    INVALID_TOKEN("EC002"),
    BLACKLISTED_TOKEN("EC003"),
    NO_MATCHING_OPERATION("EC004"),
    ILLEGAL_ACTION("EC005"),
    WRONG_PASSWORD("EC006");

    ///
    private final String value;

    ///
}
