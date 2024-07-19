package io.github.clamentos.grapher.auth.configuration;

///
import io.github.clamentos.grapher.auth.business.services.SessionService;

///..
import io.github.clamentos.grapher.auth.persistence.repositories.LogRepository;

///..
import io.github.clamentos.grapher.auth.tasks.LogsDumpingTask;

///.
import org.springframework.beans.factory.annotation.Autowired;

///..
import org.springframework.context.annotation.Configuration;

///..
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

///
@Configuration
@EnableScheduling

///
public class SchedulingConfiguration {

    ///
    private final LogsDumpingTask logsDumpingTask;
    private final SessionService sessionService;

    ///
    @Autowired
    public SchedulingConfiguration(LogRepository repository, SessionService sessionService) {

        logsDumpingTask = new LogsDumpingTask(repository);
        this.sessionService = sessionService;
    }

    ///
    @Scheduled(fixedDelay = 300000)
    public void dumpLogs() {

        logsDumpingTask.run();
    }

    ///..
    @Scheduled(fixedDelay = 300000)
    public void removeExpired() {

        sessionService.removeExpired();
    }

    ///
}
