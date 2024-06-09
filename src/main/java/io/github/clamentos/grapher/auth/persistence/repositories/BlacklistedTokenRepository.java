package io.github.clamentos.grapher.auth.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {}
