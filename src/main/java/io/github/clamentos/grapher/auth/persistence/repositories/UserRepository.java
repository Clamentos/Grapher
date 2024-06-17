package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.User;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface UserRepository extends JpaRepository<User, Long> {

    ///
    @Query(value = "SELECT COUNT(1) FROM grapher_user WHERE username = ?1", nativeQuery = true)
    boolean existsByUsername(String username);

    ///..
    @Query(

        value = "SELECT u FROM User AS u INNER JOIN FETCH u.instantAudit AS ia LEFT JOIN fetch u.operations AS o WHERE u.id = ?1"
    )
    User findFullById(long id);

    ///..
    @Query(

        value = "SELECT u FROM User AS u INNER JOIN FETCH u.instantAudit AS ia LEFT JOIN fetch u.operations AS o WHERE u.username = ?1"
    )
    User findFullByUsername(String username);

    ///
}
