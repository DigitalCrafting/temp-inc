package io.kontakt.apps.anomaly.detector.timed;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * Temporary storage for timed readings.
 * <p>
 * We have 2 storages, because timed and quantitative readings have different criteria
 * for how many of them we need.
 * <p>
 * This storage is basically an LRU cache, that will evict readings
 * no longer satisfying the criteria for storage.
 */
@Component
public class TimedRecentReadingsCache implements RecentReadingsCache {

    private final long threshold;
    private final Deque<TemperatureReading> readings = new LinkedList<>();

    public TimedRecentReadingsCache(@Value("${io.kontakt.anomaly.detector.timed.storage.threshold:10}")
                                    long threshold) {
        this.threshold = threshold;
    }

    @Override
    public synchronized Optional<Set<TemperatureReading>> add(TemperatureReading reading) {
        Set<TemperatureReading> evicted = new HashSet<>();
        if (!readings.isEmpty()) {
            /* We take into account that there can be more readings to poll from queue */
            TemperatureReading headReading = readings.peekFirst();
            while (readings.peekFirst() != null && Duration.between(headReading.timestamp(), reading.timestamp()).getSeconds() > threshold) {
                evicted.add(readings.pollFirst());
                headReading = readings.peekFirst();
            }
        }
        readings.add(reading);
        if (!evicted.isEmpty()) {
            return Optional.of(evicted);
        }
        return Optional.empty();
    }

    @Override
    public List<TemperatureReading> get() {
        return readings.stream().toList();
    }
}
