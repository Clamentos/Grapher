package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Session;

///.
import org.springframework.data.jpa.repository.JpaRepository;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface SessionRepository extends JpaRepository<Session, String> {}

///
