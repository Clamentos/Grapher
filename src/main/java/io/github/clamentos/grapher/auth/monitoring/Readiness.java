package io.github.clamentos.grapher.auth.monitoring;

///
import java.time.Duration;

///..
import java.util.concurrent.atomic.AtomicBoolean;

///.
import lombok.Getter;
import lombok.Setter;

///.
import org.springframework.stereotype.Component;

///
@Component
@Getter
@Setter

///
/**
 * <h3>Readiness</h3>
 * Spring {@link Component} that provides information about current service unavailability.
*/

///
public class Readiness {

    ///
    private final AtomicBoolean isReady;
    private volatile Duration downDuration;

    ///
    /** This class is a Spring bean and this constructor should never be called explicitly. */
    public Readiness() {

        isReady = new AtomicBoolean(true);
    }

    ///
}
