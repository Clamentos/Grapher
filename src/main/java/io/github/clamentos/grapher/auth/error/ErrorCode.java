package io.github.clamentos.grapher.auth.error;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum ErrorCode { // TODO: finish

    ///
    INVALID_AUTH_HEADER("EC000"),
    EXPIRED_SESSION("EC001"),
    ILLEGAL_ACTION("EC002"),
    ILLEGAL_ACTION_SAME_USER(""),
    ILLEGAL_ACTION_DIFFERENT_USER(""),
    ILLEGAL_ACTION_SAME_PASSWORD(""),
    WRONG_PASSWORD("EC003"),
    NOT_ENOUGH_PRIVILEGES("EC004"),

    USER_NOT_FOUND("EC005"),
    USER_ALREADY_EXISTS("EC006"),
    USER_LOCKED("EC007"),
    USER_PASSWORD_EXPIRED(""),

    BAD_FORMAT("EC008"),
    VALIDATOR_REQUIRE_NULL("EC009"),
    VALIDATOR_REQUIRE_NULL_OR_EMPTY(""),
    VALIDATOR_REQUIRE_NOT_NULL("EC010"),
    VALIDATOR_REQUIRE_FILLED("EC011"),
    VALIDATOR_PASSWORD_TOO_WEAK("EC012"),
    VALIDATOR_BAD_EMAIL(""),
    VALIDATOR_BAD_USERNAME(""),
    VALIDATOR_IMAGE_TOO_LARGE(""),
    VALIDATOR_REQUIRE_NULL_OR_FILLED(""),

    SUBSCRIPTION_NOT_FOUND(""),

    TOO_MANY_SESSIONS("EC013"),
    SESSION_NOT_FOUND("EC014"),
    DEFAULT("EC999");

    ///
    private final String value;

    ///
    /** @return The never {@code null} default error code. */
    public static ErrorCode getDefault() { return(ErrorCode.DEFAULT); }

    ///
}
