package io.github.clamentos.grapher.auth.web.controllers;

///
import io.github.clamentos.grapher.auth.business.services.ObservabilityService;

///..
import io.github.clamentos.grapher.auth.persistence.entities.Audit;
import io.github.clamentos.grapher.auth.persistence.entities.Log;

///..
import io.github.clamentos.grapher.auth.web.RequestInterceptor;

///..
import io.github.clamentos.grapher.auth.web.dtos.AuditSearchFilter;
import io.github.clamentos.grapher.auth.web.dtos.LogSearchFilter;

///.
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

///.
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///..
import java.util.concurrent.atomic.AtomicLong;

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

    ///..
    private final Runtime runtime;
    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;
    private final ThreadMXBean threadBean;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public ObservabilityController(ObservabilityService observabilityService) {

        this.observabilityService = observabilityService;

        runtime = Runtime.getRuntime();
        memoryBean = ManagementFactory.getMemoryMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
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

        Map<String, Object> status = new HashMap<>();

        status.put("freeMemory", runtime.freeMemory());
        status.put("maxMemory", runtime.maxMemory());
        status.put("totalMemory", runtime.totalMemory());
        status.put("heapMemoryUsage", memoryBean.getHeapMemoryUsage());
        status.put("nonHeapMemoryUsage", memoryBean.getNonHeapMemoryUsage());
        status.put("classPath", runtimeBean.getClassPath());
        status.put("inputArguments", runtimeBean.getInputArguments());
        status.put("pid", runtimeBean.getPid());
        status.put("startTime", runtimeBean.getStartTime());
        status.put("uptime", runtimeBean.getUptime());
        status.put("vmName", runtimeBean.getVmName());
        status.put("vmVendor", runtimeBean.getVmVendor());
        status.put("vmVersion", runtimeBean.getVmVersion());
        status.put("threadCount", threadBean.getThreadCount());
        status.put("peakThreadCount", threadBean.getPeakThreadCount());

        Map<String, Long> requestStatuses = new HashMap<>();

        for(Map.Entry<Integer, AtomicLong> entry : RequestInterceptor.requestStatusStatistics.entrySet()) {

            requestStatuses.put(Integer.toString(entry.getKey()), entry.getValue().get());
        }

        status.put("requestStatusCount", requestStatuses);
        status.put("requestCount", RequestInterceptor.requests.get());

        return(ResponseEntity.ok(status));
    }

    ///..
    @GetMapping(path = "/audits", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Audit>> getAllAuditsByFilter(@RequestBody AuditSearchFilter searchFilter)
    throws DataAccessException, IllegalArgumentException {

        return(ResponseEntity.ok(observabilityService.getAllAuditsByFilter(searchFilter)));
    }

    ///..
    @GetMapping(path = "/logs", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Log>> getAllLogsByFilter(@RequestBody LogSearchFilter searchFilter)
    throws DataAccessException, IllegalArgumentException {

        return(ResponseEntity.ok(observabilityService.getAllLogsByFilter(searchFilter)));
    }

    ///..
    @DeleteMapping(path = "/audits")
    public ResponseEntity<Void> deleteAllAuditsByPeriod(

        @RequestParam(name = "start") long start,
        @RequestParam(name = "end") long end

    ) throws DataAccessException {

        observabilityService.deleteAllAuditsByPeriod(start, end);
        return(ResponseEntity.ok().build());
    }

    ///..
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
