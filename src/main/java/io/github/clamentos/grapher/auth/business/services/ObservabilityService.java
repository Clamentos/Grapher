package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;

///.
import java.util.List;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.data.domain.PageRequest;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///
/**
 * <h3>Observability Service</h3>
 * Spring {@link Service} to manage {@link Audit} and {@link Log} objects.
*/

///
@Service

///
public class ObservabilityService {

    ///
    private final AuditRepository auditRepository;
    private final LogRepository logRepository;
    private final ValidatorService validatorService;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public ObservabilityService(AuditRepository auditRepository, LogRepository logRepository, ValidatorService validatorService) {

        this.auditRepository = auditRepository;
        this.logRepository = logRepository;
        this.validatorService = validatorService;
    }

    ///
    /**
     * Gets all the audits that match the provided search filter.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of audits.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public List<Audit> getAllAuditsByFilter(AuditSearchFilter searchFilter) throws DataAccessException, IllegalArgumentException {

        validatorService.validateAuditSearchFilter(searchFilter);
        if(searchFilter.getRecordId() != null) return(auditRepository.findAllByRecordId(searchFilter.getRecordId()));

        return(auditRepository.findAllByFilter(

            searchFilter.getTableNames(),
            searchFilter.getAuditActions(),
            searchFilter.getCreatedAtStart(),
            searchFilter.getCreatedAtEnd(),
            searchFilter.getCreatedByNames(),
            PageRequest.of(searchFilter.getPageNumber(), searchFilter.getPageSize())
        ));
    }

    ///..
    /**
     * @return The total number of logs.
     * @throws DataAccessException If any database access error occurs.
    */
    public long countAllLogs() throws DataAccessException {

        return(logRepository.count());
    }

    ///..
    /**
     * Gets all the logs that match the provided search filter.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of logs.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public List<Log> getAllLogsByFilter(LogSearchFilter searchFilter) throws DataAccessException, IllegalArgumentException {

        validatorService.validateLogSearchFilter(searchFilter);

        return(logRepository.findAllByFilter(

            searchFilter.getTimestampStart(),
            searchFilter.getTimestampEnd(),
            searchFilter.getLevels(),
            searchFilter.getThreads(),
            searchFilter.getMessageLike(),
            searchFilter.getCreatedAtStart(),
            searchFilter.getCreatedAtEnd(),
            PageRequest.of(searchFilter.getPageNumber(), searchFilter.getPageSize())
        ));
    }

    ///..
    /**
     * Deletes all the audits that have been created in the specified period.
     * @param start : The start of the period.
     * @param end : The end of the period.
     * @throws DataAccessException If any database access error occurs.
    */
    @Transactional
    public void deleteAllAuditsByPeriod(long start, long end) throws DataAccessException {

        auditRepository.deleteAllByPeriod(start, end);
    }

    ///..
    /**
     * Deletes all the logs that have been created in the specified period.
     * @param start : The start of the period.
     * @param end : The end of the period.
     * @throws DataAccessException If any database access error occurs.
    */
    @Transactional
    public void deleteAllLogsByPeriod(long start, long end) throws DataAccessException {

        logRepository.deleteAllByPeriod(start, end);
    }

    ///
}
