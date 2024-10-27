package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.UserRole;

///..
import io.github.clamentos.grapher.auth.persistence.entities.User;

///.
import java.util.List;
import java.util.Set;

///.
import org.springframework.data.domain.Pageable;

///..
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.data.repository.query.Param;

///..
import org.springframework.stereotype.Repository;

///
/**
 * <h3>User Repository</h3>
 * JPA {@link Repository} for the {@link User} entity.
*/

///
@Repository

///
public interface UserRepository extends JpaRepository<User, Long> {

    ///
    /**
     * Checks if the specified username exists.
     * @param username : The target username to check.
     * @return {@code true} if {@code username} exists in the database, {@code false} otherwise.
    */
    @Query(value = "SELECT COUNT(1) > 0 FROM User AS u WHERE u.username = ?1")
    boolean existsByUsername(String username);

    ///..
    /**
     * Finds the specified user.
     * @param id : The target user id.
     * @return The possibly {@code null}, fully populated user entity.
    */
    @Query(value = "SELECT u FROM User AS u LEFT JOIN FETCH u.subscriptions AS ss WHERE u.id = ?1")
    User findFullById(long id);

    ///..
    /**
     * Finds the specified user.
     * @param username : The target username.
     * @return The possibly {@code null}, fully populated user entity.
    */
    @Query(value = "SELECT u FROM User AS u LEFT JOIN FETCH u.subscriptions AS ss WHERE u.username = ?1")
    User findFullByUsername(String username);

    ///..
    /**
     * Finds all the users with the specified username pattern.
     * @param usernameLike : The username pattern used in the {@code LIKE} clause.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of found users within the specified page.
    */
    @Query(

        value = "SELECT new User(u.id, u.username, u.email, u.role, u.failedAccesses, u.lockedUntil, u.lockReason, " +
                "u.passwordLastChangedAt, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy) FROM User AS u WHERE u.username LIKE ?1%"
    )
    List<User> findAllMinimalByUsername(String usernameLike, Pageable pageRequest);

    ///..
    /**
     * Finds all the users with the specified email pattern.
     * @param emailLike : The email pattern used in the {@code LIKE} clause.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of found users within the specified page.
    */
    @Query(

        value = "SELECT new User(u.id, u.username, u.email, u.role, u.failedAccesses, u.lockedUntil, u.lockReason, " +
                "u.passwordLastChangedAt, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy) FROM User AS u WHERE u.email LIKE ?1%"
    )
    List<User> findAllMinimalByEmail(String emailLike, Pageable pageRequest);

    ///..
    /**
     * Finds all the users with the specified username and email patterns.
     * @param usernameLike : The username pattern used in the {@code LIKE} clause.
     * @param emailLike : The email pattern used in the {@code LIKE} clause.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of found users within the specified page.
    */
    @Query(

        value = "SELECT new User(u.id, u.username, u.email, u.role, u.failedAccesses, u.lockedUntil, u.lockReason, " +
                "u.passwordLastChangedAt, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy) FROM User AS u WHERE " +
                "u.username LIKE ?1% AND u.email LIKE ?2%"
    )
    List<User> findAllMinimalByUsernameAndEmail(String usernameLike, String emailLike, Pageable pageRequest);

    ///..
    /**
     * Finds all the users that match the specified filter.
     * @param roles : The list of roles {@code IN} clause. (cannot be {@code null} or empty).
     * @param createdAtStart : The user creation start date.
     * @param createdAtEnd : The user creation end date.
     * @param updatedAtStart : The user update start date.
     * @param updatedAtEnd : The user update start date.
     * @param createdByNames : The list of usernames of creation. (can be {@code null}, but not empty).
     * @param updatedByNames : The list of usernames of update. (can be {@code null}, but not empty).
     * @param lockedCheckMode : The locked flag. {@code true} if the users must be locked, {@code false} otherwise,
     * {@code null} if ignore.
     * @param now : The current UNIX timestamp, used to check for expired accounts / expired passwords.
     * @param passwordCheckMode : The password expire flag. {@code true} if the users must have their password expired,
     * {@code false} otherwise, {@code null} if ignore.
     * @param passwordCheckOffset : The password expiration offset from {@code now}.
     * Only used if {@code passwordCheckMode}is not {@code null}
     * @param failedAccesses : The number of failed logins.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of found users within the specified page.
    */
    @Query(

        value = "SELECT new User(u.id, u.username, u.email, u.role, u.failedAccesses, u.lockedUntil, u.lockReason, " +
        "u.passwordLastChangedAt, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy) FROM User AS u WHERE u.role IN :ro " +
        "AND u.createdAt BETWEEN :cs AND :ce " +
        "AND u.updatedAt BETWEEN :us AND :ue " +
        "AND (:cb is not null AND u.createdBy IN :cb) " +
        "AND (:ub is not null AND u.updatedBy IN :ub) " +
        "AND u.failedAccesses >= :fa " +
        "AND ((:lcm = true AND u.lockedUntil >= :now) OR (:lcm = false AND u.lockedUntil < :now) OR :lcm is null) " +
        "AND ((:pcm = true AND u.passwordLastChangedAt + :pco < :now) OR (:pcm = false AND u.passwordLastChangedAt + :pco >= :now) OR :pcm is null)"
    )
    List<User> findAllMinimalByFilters(

        @Param("ro") Set<UserRole> roles,
        @Param("cs") long createdAtStart,
        @Param("ce") long createdAtEnd,
        @Param("us") long updatedAtStart,
        @Param("ue") long updatedAtEnd,
        @Param("cb") Set<String> createdByNames,
        @Param("ub") Set<String> updatedByNames,
        @Param("lcm") Boolean lockedCheckMode,
        @Param("now") long now,
        @Param("pcm") Boolean passwordCheckMode,
        @Param("pco") long passwordCheckOffset,
        @Param("fa") short failedAccesses,
        Pageable pageRequest
    );

    ///..
    /**
     * Finds all the users that match the specified filter.
     * @param subscribedTo : The list of subscriptions. (cannot be {@code null} or empty).
     * @param roles : The list of roles {@code IN} clause. (cannot be {@code null} or empty).
     * @param createdAtStart : The user creation start date.
     * @param createdAtEnd : The user creation end date.
     * @param updatedAtStart : The user update start date.
     * @param updatedAtEnd : The user update start date.
     * @param createdByNames : The list of usernames of creation. (can be {@code null}, but not empty).
     * @param updatedByNames : The list of usernames of update. (can be {@code null}, but not empty).
     * @param lockedCheckMode : The locked flag. {@code true} if the users must be locked, {@code false} otherwise,
     * {@code null} if ignore.
     * @param now : The current UNIX timestamp, used to check for expired accounts / expired passwords.
     * @param passwordCheckMode : The password expire flag. {@code true} if the users must have their password expired,
     * {@code false} otherwise, {@code null} if ignore.
     * @param passwordCheckOffset : The password expiration offset from {@code now}.
     * Only used if {@code passwordCheckMode}is not {@code null}
     * @param failedAccesses : The number of failed logins.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of found users within the specified page.
    */
    @Query(

        value = "SELECT new User(u.id, u.username, u.email, u.role, u.failedAccesses, u.lockedUntil, u.lockReason, " +
        "u.passwordLastChangedAt, u.createdAt, u.createdBy, u.updatedAt, u.updatedBy) FROM User AS u INNER JOIN u.subscriptions AS s " +
        "INNER JOIN s.publisher AS p WHERE p.username IN :st " +
        "AND u.role IN :ro " +
        "AND u.createdAt BETWEEN :cs AND :ce " +
        "AND u.updatedAt BETWEEN :us AND :ue " +
        "AND (:cb is not null AND u.createdBy IN :cb) " +
        "AND (:ub is not null AND u.updatedBy IN :ub) " +
        "AND u.failedAccesses >= :fa " +
        "AND ((:lcm = true AND u.lockedUntil >= :now) OR (:lcm = false AND u.lockedUntil < :now) OR :lcm is null) " +
        "AND ((:pcm = true AND u.passwordLastChangedAt + :pco < :now) OR (:pcm = false AND u.passwordLastChangedAt + :pco >= :now) OR :pcm is null)"
    )
    List<User> findAllMinimalByFilters(

        @Param("st") Set<String> subscribedTo,
        @Param("ro") Set<UserRole> roles,
        @Param("cs") long createdAtStart,
        @Param("ce") long createdAtEnd,
        @Param("us") long updatedAtStart,
        @Param("ue") long updatedAtEnd,
        @Param("cb") Set<String> createdByNames,
        @Param("ub") Set<String> updatedByNames,
        @Param("lcm") Boolean lockedCheckMode,
        @Param("now") long now,
        @Param("pcm") Boolean passwordCheckMode,
        @Param("pco") long passwordCheckOffset,
        @Param("fa") short failedAccesses,
        Pageable pageRequest
    );

    ///..
    /**
     * Counts the number of subscribers of the specified user.
     * @param id : The id of the target user.
     * @return The number of subscribers.
    */
    @Query(value = "SELECT COUNT(1) FROM User AS u INNER JOIN u.subscribers AS uu")
    long countSubscribers(long id);

    ///
}
