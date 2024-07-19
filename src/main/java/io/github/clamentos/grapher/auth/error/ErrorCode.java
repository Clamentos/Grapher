package io.github.clamentos.grapher.auth.error;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum ErrorCode {

    ///
    INVALID_AUTH_HEADER("EC000"),
    EXPIRED_SESSION("EC001"),
    ILLEGAL_ACTION("EC002"),
    WRONG_PASSWORD("EC003"),
    NOT_ENOUGH_PRIVILEGES("EC004"),

    OPERATION_NOT_FOUND("EC005"),
    OPERATION_ALREADY_EXISTS("EC006"),

    USER_NOT_FOUND("EC007"),
    USER_ALREADY_EXISTS("EC008"),

    PERMISSION_NOT_FOUND("EC009"),
    PERMISSION_ALREADY_EXISTS("EC010"),

    BAD_FORMAT("EC011"),
    VALIDATOR_REQUIRE_NULL("EC012"),
    VALIDATOR_REQUIRE_NOT_NULL("EC013"),
    VALIDATOR_REQUIRE_FILLED("EC014"),

    TOO_MANY_SESSIONS("EC015"),
    SESSION_NOT_FOUND("EC016");

    ///
    private final String value;

    ///
    public static boolean isValidErrorCode(String code) {

        switch(code) {

            case "EC000": return(true);
            case "EC001": return(true);
            case "EC002": return(true);
            case "EC003": return(true);
            case "EC004": return(true);
            case "EC005": return(true);
            case "EC006": return(true);
            case "EC007": return(true);
            case "EC008": return(true);
            case "EC009": return(true);
            case "EC010": return(true);
            case "EC011": return(true);
            case "EC012": return(true);
            case "EC013": return(true);

            default: return(false);
        }
    }

    ///
}
