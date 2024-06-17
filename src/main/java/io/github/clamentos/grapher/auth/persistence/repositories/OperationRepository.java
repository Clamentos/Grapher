package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Operation;
import io.github.clamentos.grapher.auth.persistence.entities.User;

///.
import java.util.List;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

///.
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface OperationRepository extends JpaRepository<Operation, Short> {

    ///
    @Query(

        value = "SELECT o FROM Operation AS o " +
                "INNER JOIN FETCH o.instantAudit AS ia INNER JOIN o.userOperations AS uo WHERE uo.user = ?1"
    )
    List<Operation> findAllByUser(User user);

    ///..
    @Query(value = "SELECT o FROM Operation AS o WHERE o.name IN ?1")
    List<Operation> findAllByNames(List<String> names);

    ///..
    @Query(value = "SELECT name FROM operation WHERE id IN ?1", nativeQuery = true)
    List<String> listExists(List<String> names);

    ///
}
