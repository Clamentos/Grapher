package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///.
import org.springframework.data.jpa.repository.JpaRepository;

///..
import org.springframework.stereotype.Repository;

///
/**
 * <h3>Session Repository</h3>
 * JPA {@link Repository} for the {@link Session} entity.
*/

///
@Repository

///
public interface SessionRepository extends JpaRepository<Session, String> {}

///
