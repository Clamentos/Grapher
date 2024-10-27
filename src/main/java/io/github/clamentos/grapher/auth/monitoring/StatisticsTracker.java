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
import java.util.LinkedHashMap;
import java.util.Map;

///..
import java.util.concurrent.ConcurrentHashMap;

///..
import java.util.concurrent.atomic.AtomicLong;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.data.domain.Range;

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
    private final Map<Integer, AtomicLong> responseStatusCounters;
    private final Map<String, TimeSamples> requestTimeSamples;

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
        requestTimeSamples = new ConcurrentHashMap<>();
        brokerRequestCounters = new ConcurrentHashMap<>();
        brokerResponseCounters = new ConcurrentHashMap<>();

        memoryBean = ManagementFactory.getMemoryMXBean();
        runtimeBean = ManagementFactory.getRuntimeMXBean();
        threadBean = ManagementFactory.getThreadMXBean();
    }

    ///
    /**
     * Increments the corresponding response status counter by one and stores the execution time.
     * @param status : The response status.
     * @param time : The execution time of the request in milliseconds.
     * @param uri : The request URI.
    */
    public void incrementResponse(int status, int time, String uri) {

        responseStatusCounters.computeIfAbsent(status, key -> new AtomicLong()).incrementAndGet();
        requestTimeSamples.computeIfAbsent(uri, key -> new TimeSamples(1024)).put(time);
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

        requestsResponsesInfo.put("responseStatusCounts", this.extract(responseStatusCounters));
        requestsResponsesInfo.put("latencyDistribution", this.calculateDistributions(requestTimeSamples));

        Map<String, Object> brokerInfo = new HashMap<>();

        brokerInfo.put("brokerRequestCounts", this.extract(brokerRequestCounters));
        brokerInfo.put("brokerResponseCounts", this.extract(brokerResponseCounters));

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

    ///.
    private <T> Map<T, Long> extract(Map<T, AtomicLong> inputMap) throws NullPointerException {

        Map<T, Long> extractedMap = new HashMap<>();

        for(Map.Entry<T, AtomicLong> entry : inputMap.entrySet()) {

            extractedMap.put(entry.getKey(), entry.getValue().get());
        }

        return(extractedMap);
    }

    ///..
    private Map<String, Map<String, Integer>> calculateDistributions(Map<String, TimeSamples> requestTimeSamples) {

        Map<String, Map<String, Integer>> distributions = new HashMap<>();

        for(Map.Entry<String, TimeSamples> entry : requestTimeSamples.entrySet()) {

            distributions.put(entry.getKey(), this.calculateDistribution(entry.getValue()));
        }

        return(distributions);
    }

    ///..
    private Map<String, Integer> calculateDistribution(TimeSamples requestTimeSamples) {

        int[] samples = requestTimeSamples.getAll();
        int max = 0;

        for(int i = 0; i < samples.length; i++) {

            max = Math.max(max, samples[i]);
        }

        // int[] always has 1 element, used as an "indirect" value.
        Map<Range<Integer>, int[]> buckets = new LinkedHashMap<>();
        int bucketSize = Math.ceilDiv(max, 20);
        int start = 0;

        for(int i = 0; i < 20; i++) {

            buckets.put(Range.rightOpen(start, start + bucketSize), new int[]{0});
            start += bucketSize;
        }

        for(int i = 0; i < samples.length; i++) {

            if(samples[i] > 0) {

                for(Map.Entry<Range<Integer>, int[]> entry : buckets.entrySet()) {

                    if(entry.getKey().contains(samples[i])) {
    
                        entry.getValue()[0]++;
                        break;
                    }
                }
            }
        }

        Map<String, Integer> distribution = new LinkedHashMap<>();

        for(Map.Entry<Range<Integer>, int[]> entry : buckets.entrySet()) {

            int lowerBound = entry.getKey().getLowerBound().getValue().get();
            int upperBound = entry.getKey().getUpperBound().getValue().get() - 1;

            distribution.put(Integer.toString(lowerBound) + "-" + Integer.toString(upperBound), entry.getValue()[0]);
        }

        return(distribution);
    }

    ///
}
