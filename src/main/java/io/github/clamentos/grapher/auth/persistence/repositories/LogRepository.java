package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///.
import java.util.List;
import java.util.Set;

///.
import org.springframework.data.domain.Pageable;

///..
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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
    /**
     * Finds All the logs that match the specified filter.
     * @param timestampStart : The log timestamp start.
     * @param timestampEnd : The log timestamp end.
     * @param levels : The log levels. (cannot be {@code null} or empty).
     * @param threads : The threads. (can be {@code null}, but not empty).
     * @param messageLike : The message like pattern.
     * @param createdAtStart : The log insertion timestamp start.
     * @param createdAtEnd : The log insertion timestamp end.
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of logs.
    */
    @Query(

        value = "SELECT l FROM Log AS l WHERE l.timestamp BETWEEN ?1 AND ?2 AND l.level IN ?3 AND " +
                "(?4 is null OR l.thread IN ?4) AND l.message LIKE %?5% AND l.createdAt BETWEEN ?6 AND ?7"
    )
    List<Log> findAllByFilter(

        long timestampStart,
        long timestampEnd,
        Set<String> levels,
        Set<String> threads,
        String messageLike,
        long createdAtStart,
        long createdAtEnd,
        Pageable pageRequest
    );

    ///..
    /**
     * Deletes all the audits that have been created in the specified period.
     * @param start : The start of the period.
     * @param end : The end of the period.
    */
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Log AS l WHERE l.createdAt BETWEEN ?1 AND ?2")
    void deleteAllByPeriod(long start, long end);

    ///
}
