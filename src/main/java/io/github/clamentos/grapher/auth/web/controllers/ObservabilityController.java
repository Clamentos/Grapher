package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.ObservabilityService;

///..
import io.github.clamentos.grapher.auth.monitoring.StatisticsTracker;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;

///.
import java.util.List;
import java.util.Map;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.dao.DataAccessException;

///..
import org.springframework.http.ResponseEntity;

///..
import org.springframework.web.bind.annotation.DeleteMapping;

///..
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

///
/**
 * <h3>Observability Controller</h3>
 * Spring {@link RestController} that exposes health and status APIs.
*/

///
@RestController
@RequestMapping(path = "grapher/v1/auth-service/observability")

///
public final class ObservabilityController {

    ///
    private final ObservabilityService observabilityService;
    private final StatisticsTracker statisticsTracker;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public ObservabilityController(ObservabilityService observabilityService, StatisticsTracker statisticsTracker) {

        this.observabilityService = observabilityService;
        this.statisticsTracker = statisticsTracker;
    }

    ///
    /** @return The never {@code null} empty HTTP response as an "alive" check. */
    @GetMapping
    public ResponseEntity<Void> aliveCheck() {

        return(ResponseEntity.ok().build());
    }

    ///..
    /** @return The never {@code null} HTTP JSON response holding the full current service status and statistics. */
    @GetMapping(path = "/status", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getStatus() {

        return(ResponseEntity.ok(statisticsTracker.getStatus()));
    }

    ///..
    /**
     * Gets all the audits that match the provided search filter.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of audits.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    @GetMapping(path = "/audits", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Audit>> getAllAuditsByFilter(@RequestBody AuditSearchFilter searchFilter)
    throws DataAccessException, IllegalArgumentException {

        return(ResponseEntity.ok(observabilityService.getAllAuditsByFilter(searchFilter)));
    }

    ///..
    @GetMapping(path = "/logs/count", produces = "text/plain")
    public ResponseEntity<Long> countAllLogs() throws DataAccessException {

        return(ResponseEntity.ok(observabilityService.countAllLogs()));
    }

    ///..
    /**
     * Gets all the logs that match the provided search filter.
     * @param searchFilter : The search filer.
     * @return The never {@code null} list of logs.
     * @throws DataAccessException If any database access error occurs.
     * @throws IllegalArgumentException If {@code searchFilter} doesn't pass validation.
    */
    @GetMapping(path = "/logs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Log>> getAllLogsByFilter(@RequestBody LogSearchFilter searchFilter)
    throws DataAccessException, IllegalArgumentException {

        return(ResponseEntity.ok(observabilityService.getAllLogsByFilter(searchFilter)));
    }

    ///..
    /**
     * Deletes all the audits that have been created in the specified period.
     * @param start : The start of the period.
     * @param end : The end of the period.
     * @throws DataAccessException If any database access error occurs.
    */
    @DeleteMapping(path = "/audits")
    public ResponseEntity<Void> deleteAllAuditsByPeriod(

        @RequestParam(name = "start") long start,
        @RequestParam(name = "end") long end

    ) throws DataAccessException {

        observabilityService.deleteAllAuditsByPeriod(start, end);
        return(ResponseEntity.ok().build());
    }

    ///..
    /**
     * Deletes all the logs that have been created in the specified period.
     * @param start : The start of the period.
     * @param end : The end of the period.
     * @throws DataAccessException If any database access error occurs.
    */
    @DeleteMapping(path = "/logs")
    public ResponseEntity<Void> deleteAllLogsByPeriod(

        @RequestParam(name = "start") long start,
        @RequestParam(name = "end") long end

    ) throws DataAccessException {

        observabilityService.deleteAllLogsByPeriod(start, end);
        return(ResponseEntity.ok().build());
    }

    ///
}
