package io.github.clamentos.grapher.auth.persistence;

///
/**
 * <h3>User Role</h3>
 * Simple enum to indicate all the possible roles for registered users.
*/

///
public enum UserRole {

    ///
    STANDARD,
    CREATOR,
    MODERATOR,
    ADMINISTRATOR;

    ///
    /** @return The never {@code null} default user role for registration. */
    public static UserRole getDefault() { return(UserRole.STANDARD); }

    ///
}
