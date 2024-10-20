package io.github.clamentos.grapher.auth.monitoring;

///
import io.github.clamentos.grapher.auth.business.services.SessionService;

///.
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

///..
import java.util.HashMap;
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicLong;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.stereotype.Service;

///
/**
 * <h3>Statistics Tracker</h3>
 * Spring {@link Service} to provide various monitoring methods.
*/

///
@Service

///
public class StatisticsTracker {

    ///
    private final SessionService sessionService;

    ///..
    private final AtomicLong incomingRequestCounter;
    private final Map<Integer, AtomicLong> responseStatusCounters;

    ///..
    private final Map<String, AtomicLong> brokerRequestCounters;
    private final Map<String, AtomicLong> brokerResponseCounters;

    ///..
    private final MemoryMXBean memoryBean;
    private final RuntimeMXBean runtimeBean;
    private final ThreadMXBean threadBean;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    @Autowired
    public StatisticsTracker(SessionService sessionService) {

        this.sessionService = sessionService;

        responseStatusCounters = new ConcurrentHashMap<>();
        incomingRequestCounter = new AtomicLong();
        brokerRequestCounters = new ConcurrentHashMap<>();
        brokerResponseCounters = new ConcurrentHashMap<>();

        memoryBean = ManagementFactory.getMemoryMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    ///
    /** Increments the requests received counter by one. */
    public void incrementIncomingRequestCount() {

        incomingRequestCounter.incrementAndGet();
    }

    ///..
    /**
     * Increments the corresponding response status counter by one.
     * @param status : The response status.
    */
    public void incrementResponseStatusCounts(int status) {

        responseStatusCounters.computeIfAbsent(status, key -> new AtomicLong()).incrementAndGet();
    }

    ///..
    /**
     * Increments the corresponding message broker request counter by one.
     * @param destination : The destination.
     * @throws NullPointerException If {@code destination} is {@code null}.
    */
    public void incrementBrokerRequestCount(String destination) throws NullPointerException {

        brokerRequestCounters.computeIfAbsent(destination, key -> new AtomicLong()).incrementAndGet();
    }

    ///..
    /**
     * Increments the corresponding message broker response counter by one.
     * @param destination : The destination.
     * @throws NullPointerException If {@code destination} is {@code null}.
    */
    public void incrementBrokerResponseCount(String destination) throws NullPointerException {

        brokerResponseCounters.computeIfAbsent(destination, key -> new AtomicLong()).incrementAndGet();
    }

    ///..
    /** @return The never {@code null} application status summary. */
    public Map<String, Object> getStatus() {

        this.incrementResponseStatusCounts(200);
        Map<String, Object> runtimeInfo = new HashMap<>();

        runtimeInfo.put("inputArguments", runtimeBean.getInputArguments());
        runtimeInfo.put("startTime", runtimeBean.getStartTime());
        runtimeInfo.put("uptime", runtimeBean.getUptime());
        runtimeInfo.put("properties", runtimeBean.getSystemProperties());

        Map<String, Object> memoryInfo = new HashMap<>();

        memoryInfo.put("heapUsage", memoryBean.getHeapMemoryUsage());
        memoryInfo.put("nonHeapUsage", memoryBean.getNonHeapMemoryUsage());

        Map<String, Object> threadsInfo = new HashMap<>();

        int threadCount = threadBean.getThreadCount();
        int daemonThreadCount = threadBean.getDaemonThreadCount();

        threadsInfo.put("threadCount", threadCount - daemonThreadCount);
        threadsInfo.put("daemonThreadCount", daemonThreadCount);
        threadsInfo.put("peakThreadCount", threadBean.getPeakThreadCount());
        threadsInfo.put("platformThreads", threadBean.dumpAllThreads(false, false, 0));

        Map<String, Object> requestsResponsesInfo = new HashMap<>();

        requestsResponsesInfo.put("incomingRequestCount", incomingRequestCounter.get());
        requestsResponsesInfo.put("responseStatusCounts", extract(responseStatusCounters));

        Map<String, Object> brokerInfo = new HashMap<>();

        brokerInfo.put("brokerRequestCounts", extract(brokerRequestCounters));
        brokerInfo.put("brokerResponseCounts", extract(brokerResponseCounters));

        Map<String, Object> status = new HashMap<>();

        status.put("runtimeInfo", runtimeInfo);
        status.put("memoryInfo", memoryInfo);
        status.put("threadsInfo", threadsInfo);
        status.put("requestsResponsesInfo", requestsResponsesInfo);
        status.put("brokerInfo", brokerInfo);
        status.put("loggedUsersCount", sessionService.getCurrentlyLoggedUsersCount());
        status.put("forgotPasswordSessionsCount", sessionService.getCurrentForgotPasswordCount());

        return(status);
    }

    ///..
    private <T> Map<T, Long> extract(Map<T, AtomicLong> inputMap) {

        Map<T, Long> extractedMap = new HashMap<>();

        for(Map.Entry<T, AtomicLong> entry : inputMap.entrySet()) {

            extractedMap.put(entry.getKey(), entry.getValue().get());
        }

        return(extractedMap);
    }

    ///
}
