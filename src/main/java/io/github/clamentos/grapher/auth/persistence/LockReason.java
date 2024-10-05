package io.github.clamentos.grapher.auth.persistence;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Lock Reason</h3>
 * Simple enum to indicate all the possible user account lock reasons.
*/

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum LockReason {

    ///
    TOO_MANY_FAILED_LOGINS("LR001");

    ///
    private final String value;

    ///
}
