package io.github.clamentos.grapher.auth.error;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)

///
/**
 * <h3>Error Code</h3>
 * Simple enumeration containing all the possible standard request failures.
*/

///
public enum ErrorCode {

    ///
    INVALID_AUTH_HEADER,

    SESSION_NOT_FOUND,
    EXPIRED_SESSION,
    TOO_MANY_SESSIONS,
    WRONG_PASSWORD,
    NOT_ENOUGH_PRIVILEGES,

    ILLEGAL_ACTION,
    ILLEGAL_ACTION_SAME_USER,
    ILLEGAL_ACTION_DIFFERENT_USER,
    ILLEGAL_ACTION_SAME_PASSWORD,

    VALIDATOR_BAD_FORMAT,
    VALIDATOR_REQUIRE_NULL,
    VALIDATOR_REQUIRE_NULL_OR_EMPTY,
    VALIDATOR_REQUIRE_NOT_NULL,
    VALIDATOR_REQUIRE_FILLED,
    VALIDATOR_REQUIRE_NULL_OR_FILLED,
    VALIDATOR_PASSWORD_TOO_WEAK,
    VALIDATOR_BAD_EMAIL,
    VALIDATOR_BAD_USERNAME,
    VALIDATOR_IMAGE_TOO_LARGE,

    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_LOCKED,
    USER_PASSWORD_EXPIRED,

    SUBSCRIPTION_NOT_FOUND,

    CUSTOM,
    UNCATEGORIZED;

    ///
    /** @return The never {@code null} default error code. */
    public static ErrorCode getDefault() { return(ErrorCode.UNCATEGORIZED); }

    ///
}
