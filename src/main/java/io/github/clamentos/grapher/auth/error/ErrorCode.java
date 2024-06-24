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
    INVALID_TOKEN("EC000"),
    BLACKLISTED_TOKEN("EC001"),
    ILLEGAL_ACTION("EC002"),
    WRONG_PASSWORD("EC003"),
    NOT_ENOUGH_PRIVILEGES("EC004"),

    OPERATION_NOT_FOUND("EC005"),
    OPERATION_ALREADY_EXISTS("EC006"),

    USER_NOT_FOUND("EC007"),
    USER_ALREADY_EXISTS("EC008"),

    PERMISSION_NOT_FOUND("EC009"),
    PERMISSION_ALREADY_EXISTS("EC010"),

    VALIDATOR_REQUIRE_NULL("EC011"),
    VALIDATOR_REQUIRE_NOT_NULL("EC012"),
    VALIDATOR_REQUIRE_FILLED("EC013");

    ///
    private final String value;

    ///
    public static boolean isValidErrorCode(String name) {

        try{

            Enum.valueOf(ErrorCode.class, name);
            return(true);
        }

        catch(IllegalArgumentException exc) {

            return(false);
        }
    }

    ///
}
