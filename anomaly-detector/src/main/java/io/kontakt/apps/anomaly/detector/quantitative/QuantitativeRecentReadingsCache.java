package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
public class QuantitativeRecentReadingsCache implements RecentReadingsCache {

    private final int threshold;
    private final Deque<TemperatureReading> readings;

    public QuantitativeRecentReadingsCache(@Value("${io.kontakt.anomaly.detector.quantitative.storage.threshold:10}")
                                           int threshold) {
        this.threshold = threshold;
        this.readings = new ArrayDeque<>(threshold);
    }

    /**
     * Since this cache is based on number of elements, we will always evict at most one.
     * */
    @Override
    public synchronized Optional<Set<TemperatureReading>> add(TemperatureReading reading) {
        TemperatureReading evicted = null;
        if (!readings.isEmpty()) {
            if (readings.size() >= threshold) {
                evicted = readings.poll();
            }
        }
        readings.add(reading);
        if (evicted != null) {
            return Optional.of(Set.of(evicted));
        }
        return Optional.empty();
    }

    @Override
    public List<TemperatureReading> get() {
        return readings.stream().toList();
    }
}
