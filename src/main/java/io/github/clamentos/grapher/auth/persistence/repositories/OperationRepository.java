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
    @Query(value = "SELECT o.id FROM Operation AS o WHERE o.id IN ?1")
    List<Short> doIdsExist(List<Short> ids);

    ///..
    @Query(value = "SELECT o.name FROM Operation AS o WHERE o.name IN ?1")
    List<String> doNamesExist(List<String> names);

    ///..
    @Override
    @Query(value = "SELECT o FROM Operation AS o INNER JOIN FETCH o.instantAudit AS ia WHERE o.id IN ?1")
    List<Operation> findAllById(Iterable<Short> ids);

    ///..
    @Query(value = "SELECT o FROM Operation AS o INNER JOIN FETCH o.instantAudit AS ia WHERE o.name IN ?1")
    List<Operation> findAllByName(List<String> names);

    ///..
    @Query(

        value = "SELECT o FROM Operation AS o " +
                "INNER JOIN FETCH o.instantAudit AS ia INNER JOIN o.userOperations AS uo WHERE uo.user = ?1"
    )
    List<Operation> findAllByUser(User user);

    ///
}
