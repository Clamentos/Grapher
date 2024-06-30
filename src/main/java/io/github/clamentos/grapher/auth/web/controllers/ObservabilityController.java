package io.github.clamentos.grapher.auth.web.controllers;

///
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

///.
import java.util.HashMap;
import java.util.Map;

///.
import org.springframework.http.ResponseEntity;

///.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

///
@RestController
@RequestMapping(path = "/v1/grapher/auth/observability")

///
public final class ObservabilityController {

    ///
    private final Runtime runtime;
    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;
    private final ThreadMXBean threadBean;

    ///
    public ObservabilityController() {

        runtime = Runtime.getRuntime();
        memoryBean = ManagementFactory.getMemoryMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    ///
    @GetMapping
    public ResponseEntity<Void> healthCheck() {

        return(ResponseEntity.ok().build());
    }

    ///..
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

        return(ResponseEntity.ok(status));
    }

    ///
}
