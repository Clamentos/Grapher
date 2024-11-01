package io.github.clamentos.grapher.auth.business;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///.
import java.util.Set;

///.
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
/**
 * <h3>Action</h3>
 * Enum that lists actions that a user might perform that require additional authorization checks.
*/

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum Action {

    ///
    /** Must have {@code ADMINISTRATOR} role.*/
    CREATE_OTHERS(Set.of(UserRole.ADMINISTRATOR)),

    /** Must have {@code MODERATOR}, {@code ADMINISTRATOR} role.*/
    SEARCH_USERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),

    /** Must have {@code MODERATOR}, {@code ADMINISTRATOR} role.*/
    SEE_PRIVATE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),

    /** Must have {@code MODERATOR}, {@code ADMINISTRATOR} role.*/
    CHANGE_ROLE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),

    /** Must have {@code MODERATOR}, {@code ADMINISTRATOR} role.*/
    CHANGE_LOCK_DATE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),

    /** Must have {@code ADMINISTRATOR} role.*/
    DELETE_OTHERS(Set.of(UserRole.ADMINISTRATOR));

    ///
    private final Set<UserRole> allowedRoles;

    ///
}
