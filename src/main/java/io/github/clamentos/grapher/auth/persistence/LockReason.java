package io.github.clamentos.grapher.auth.persistence;

///
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

///
/**
 * <h3>Lock Reason</h3>
 * Simple enum to indicate all the possible user account lock reasons given by the system (non-custom ones).
*/

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)

///
public enum LockReason {

    ///
    TOO_MANY_FAILED_LOGINS,
    CUSTOM;

    ///
}
