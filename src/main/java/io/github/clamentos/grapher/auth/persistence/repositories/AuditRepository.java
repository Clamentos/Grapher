package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Audit;

///.
import org.springframework.data.jpa.repository.JpaRepository;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface AuditRepository extends JpaRepository<Audit, Long> {

    ///
    // ...

    ///
}
