package io.github.clamentos.grapher.auth.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.clamentos.grapher.auth.persistence.entities.Audit;

public interface AuditRepository extends JpaRepository<Audit, Long> {}
