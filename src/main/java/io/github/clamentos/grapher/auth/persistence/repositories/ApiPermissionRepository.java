package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.ApiPermission;
import io.github.clamentos.grapher.auth.persistence.entities.Operation;

///.
import java.util.List;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface ApiPermissionRepository extends JpaRepository<ApiPermission, Long> {

    ///
    @Query(value = "SELECT p.id FROM ApiPermission AS p WHERE p.path IN ?1 AND p.operation IN ?2")
    List<Long> findAllIdsByCombo(Iterable<String> paths, Iterable<Operation> operations);

    ///..
    @Query(value = "SELECT p FROM ApiPermission AS p INNER JOIN FETCH p.instantAudit AS ia INNER JOIN FETCH p.operation AS o")
    List<ApiPermission> findAll();

    ///..
    @Query(value = "SELECT p FROM ApiPermission AS p INNER JOIN FETCH p.operation AS o")
    List<ApiPermission> findAllMinimal();

    ///..
    @Override
    @Query(

        value = "SELECT p FROM ApiPermission AS p INNER JOIN FETCH p.instantAudit AS ia INNER JOIN FETCH p.operation AS o " +
                "WHERE p.id IN ?1"
    )
    List<ApiPermission> findAllById(Iterable<Long> ids);

    ///
}
