package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.User;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    ///
    @Query(value = "SELECT COUNT(1) FROM User AS u WHERE u.username = ?1")
    boolean existsByUsername(String username);

    ///..
    @Query(value = "SELECT u FROM User AS u INNER JOIN FETCH u.instantAudit AS ia WHERE u.id = ?1")
    User findByIdMinimal(long id);

    ///..
    @Query(

        value = "SELECT u FROM User AS u INNER JOIN FETCH u.instantAudit AS ia LEFT JOIN FETCH u.operations AS o WHERE u.id = ?1"
    )
    User findById(long id);

    ///..
    @Query(

        value = "SELECT u FROM User AS u INNER JOIN FETCH u.instantAudit AS ia LEFT JOIN FETCH u.operations AS o WHERE u.username = ?1"
    )
    User findByUsername(String username);

    ///
}
