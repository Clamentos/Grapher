package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.BlacklistedToken;

///.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.stereotype.Repository;

///
@Repository

///
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {

    ///
    @Modifying
    @Query(value = "DELETE FROM BlacklistedToken AS bt WHERE bt.expiresAt <= ?1")
    void deleteAllExpired(long deadline);

    ///
}
