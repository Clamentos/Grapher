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
    /**
     * Decodes the ordinal into the corresponding enum constant.
     * @param ordinal : The ordinal to decode
     * @return The never {@code null} corresponding enum constant.
     * @throws IllegalArgumentException If {@code ordinal} doesn't match any enum constant.
    */
    public static UserRole decode(byte ordinal) throws IllegalArgumentException {

        switch(ordinal) {

            case 0: return(UserRole.STANDARD);
            case 1: return(UserRole.CREATOR);
            case 2: return(UserRole.MODERATOR);
            case 3: return(UserRole.ADMINISTRATOR);

            default: throw new IllegalArgumentException("Unknown ordinal: " + ordinal);
        }
    }

    ///..
    /** @return The never {@code null} default user role for registration. */
    public static UserRole getDefault() { return(UserRole.STANDARD); }

    ///
}
