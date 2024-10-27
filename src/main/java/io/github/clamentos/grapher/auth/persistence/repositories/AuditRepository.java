package io.github.clamentos.grapher.auth.persistence.repositories;

///
import io.github.clamentos.grapher.auth.persistence.AuditAction;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;

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
 * <h3>Audit Repository</h3>
 * JPA {@link Repository} for the {@link Audit} entity.
*/

///
@Repository

///
public interface AuditRepository extends JpaRepository<Audit, Long> {

    ///
    /**
     * Finds all the audits that match the given record id.
     * @param recordId : The record id.
     * @return The never {@code null} list of audits.
    */
    @Query(value = "SELECT a FROM Audit AS a WHERE a.recordId = ?1")
    List<Audit> findAllByRecordId(long recordId);

    ///..
    /**
     * Finds all the audits that match the specified filter.
     * @param tableNames : The name of the tables. (cannot be {@code null} or empty).
     * @param auditActions : The audit actions. (cannot be {@code null} or empty).
     * @param createdAtStart : The audit creation start date.
     * @param createdAtEnd : The audit creation end date.
     * @param createdByNames : The list of usernames of creation. (can be {@code null}, but not empty).
     * @param pageRequest : The target page to fetch.
     * @return The never {@code null} list of audits.
    */
    @Query(

        value = "SELECT a FROM Audit AS a WHERE a.tableName IN ?1 AND a.action IN ?2 AND a.createdAt BETWEEN ?3 AND ?4 " +
                "AND (?5 is null OR a.createdBy IN ?5)"
    )
    List<Audit> findAllByFilter(

        Set<String> tableNames,
        Set<AuditAction> auditActions,
        long createdAtStart,
        long createdAtEnd,
        Set<String> createdByNames,
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
    @Query(value = "DELETE FROM Audit AS a WHERE a.createdAt BETWEEN ?1 AND ?2")
    void deleteAllByPeriod(long start, long end);

    ///
}
