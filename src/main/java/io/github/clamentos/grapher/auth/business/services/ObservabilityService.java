package io.github.clamentos.grapher.auth.business.services;

///
import io.github.clamentos.grapher.auth.monitoring.Readiness;
import io.github.clamentos.grapher.auth.monitoring.StatisticsTracker;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.AuditRepository;
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilterDto;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilterDto;

///.
import java.time.Duration;

///..
import java.time.format.DateTimeParseException;

///..
import java.util.List;
import java.util.Map;

///.
import lombok.extern.slf4j.Slf4j;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;

///..
import org.springframework.context.ApplicationContext;

///..
import org.springframework.context.event.EventListener;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.data.domain.PageRequest;

///..
import org.springframework.scheduling.annotation.Async;

///..
import org.springframework.stereotype.Service;

///..
import org.springframework.transaction.annotation.Transactional;

///..
import org.springframework.web.context.support.ServletRequestHandledEvent;

///
/**
 * <h3>Observability Service</h3>
 * Spring {@link Service} to manage {@link Audit} and {@link Log} objects.
*/

///
@Service
@Slf4j

///
public class ObservabilityService {

    ///
    private final AuditRepository auditRepository;
    private final LogRepository logRepository;
    private final ValidatorService validatorService;
    private final StatisticsTracker statisticsTracker;

    ///..
    private final ApplicationContext applicationContext;

    ///..
    private final Readiness readiness;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public ObservabilityService(

        AuditRepository auditRepository,
        LogRepository logRepository,
        ValidatorService validatorService,
        StatisticsTracker statisticsTracker,
        ApplicationContext applicationContext,
        Readiness readiness
    ) {

        this.auditRepository = auditRepository;
        this.logRepository = logRepository;
        this.validatorService = validatorService;
        this.statisticsTracker = statisticsTracker;
        this.applicationContext = applicationContext;
        this.readiness = readiness;
    }

    ///
    /** @return The never {@code null} application status summary. */
    public Map<String, Object> getStatus() {

        return(statisticsTracker.getStatus());
    }

    ///..
    /**
     * Gets all the audits that match the provided search filter.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of audits.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    public List<Audit> getAllAuditsByFilter(AuditSearchFilterDto searchFilter) throws DataAccessException, IllegalArgumentException {

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
    public List<Log> getAllLogsByFilter(LogSearchFilterDto searchFilter) throws DataAccessException, IllegalArgumentException {

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
     * Sets the readiness state of this application to {@link ReadinessState#REFUSING_TRAFFIC} for the specified duration.
     * @param duration : The downtime duration, such as {@code PnDTnHnMn.nS}, formatted according to the {@code ISO-8601} format.
     * @throws NullPointerException If {@code duration} is {@code null}.
     * @throws DateTimeParseException If {@code duration} cannot be parsed.
    */
    @Async
    public void notReadyFor(String duration) throws DateTimeParseException, NullPointerException {

        if(readiness.getIsReady().compareAndSet(true, false)) {

            log.info("Refusing traffic for {}", duration);

            Duration downDuration = Duration.parse(duration);
            readiness.setDownDuration(downDuration);

            AvailabilityChangeEvent.publish(applicationContext, ReadinessState.REFUSING_TRAFFIC);

            try {

                Thread.sleep(downDuration);
            }

            catch(InterruptedException exc) {

                Thread.currentThread().interrupt();
                log.warn("Interrupted, restoring readiness");
            }

            readiness.setDownDuration(null);
            readiness.getIsReady().set(true);

            AvailabilityChangeEvent.publish(applicationContext, ReadinessState.ACCEPTING_TRAFFIC);
            log.info("Accepting traffic");
        }
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

    ///.
    @EventListener
    protected void handleRequestHandledEvent(ServletRequestHandledEvent event) {

        String uri = event.getMethod() + event.getRequestUrl();
        statisticsTracker.incrementResponse(event.getStatusCode(), (int)event.getProcessingTimeMillis(), uri);
    }

    ///
}
