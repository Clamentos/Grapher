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
    CREATE_OTHERS(Set.of(UserRole.ADMINISTRATOR)),
    SEARCH_USERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),
    SEE_PRIVATE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),
    CHANGE_ROLE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),
    CHANGE_LOCK_DATE_OTHERS(Set.of(UserRole.MODERATOR, UserRole.ADMINISTRATOR)),
    DELETE_OTHERS(Set.of(UserRole.ADMINISTRATOR));

    ///
    private final Set<UserRole> allowedRoles;

    ///
}
