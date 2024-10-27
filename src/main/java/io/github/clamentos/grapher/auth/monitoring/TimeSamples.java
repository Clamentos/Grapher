package io.github.clamentos.grapher.auth.monitoring;

///
import java.util.concurrent.atomic.AtomicInteger;

///
/**
 * <h3>Time Samples</h3>
 * Concurrent ring buffer of time samples.
*/

///
public final class TimeSamples {

    ///
    private final AtomicInteger[] samples;
    private final AtomicInteger position;

    ///
    /**
     * Constructs a new {@link TimeSamples} object with the given size.
     * @param size : The size of the collection.
     * @throws IllegalArgumentException If {@code size < 1}.
    */
    public TimeSamples(int size) throws IllegalArgumentException {

        if(size < 1) throw new IllegalArgumentException("The size must be at least 1");

        samples = new AtomicInteger[size];
        for(int i = 0; i < size; i++) samples[i] = new AtomicInteger();

        position = new AtomicInteger();
    }

    ///
    /**
     * Inserts a new sample into the buffer and advanced the position by one.
     * @param sample : The time sample to insert.
    */
    public void put(int sample) {

        samples[position.getAndUpdate(val -> (val + 1) % samples.length)].set(sample);
    }

    ///..
    /** @return The never {@code null} and never empty array of all samples. */
    public int[] getAll() {

        int[] data = new int[samples.length];
        for(int i = 0; i < data.length; i++) data[i] = samples[i].get();

        return(data);
    }

    ///
}
