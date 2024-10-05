package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///.
import java.util.List;

///.
import org.springframework.data.domain.Pageable;

///..
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

///..
import org.springframework.data.repository.query.Param;

///..
import org.springframework.stereotype.Repository;

///..
import org.springframework.transaction.annotation.Transactional;

///
/**
 * <h3>Log Repository</h3>
 * JPA {@link Repository} for the {@link Log} entity.
*/

///
@Repository

///
public interface LogRepository extends JpaRepository<Log, Long> {

    ///
    @Query(

        value = "SELECT l FROM Log AS l WHERE l.timestamp BETWEEN :ts AND :te AND l.level IN :l AND " +
                "(:t is null OR l.thread IN :t) AND l.message LIKE %:m% AND l.createdAt BETWEEN :cs AND :ce"
    )
    List<Log> findAllByFilter(

        @Param("ts") long timestampStart,
        @Param("te") long timestampEnd,
        @Param("l") List<String> levels,
        @Param("t") List<String> threads,
        @Param("m") String message,
        @Param("cs") long createdAtStart,
        @Param("ce") long createdAtEnd,
        Pageable pageRequest
    );

    ///..
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Log AS l WHERE l.createdAt BETWEEN ?1 AND ?2")
    void deleteAllByPeriod(long start, long end);

    ///
}
