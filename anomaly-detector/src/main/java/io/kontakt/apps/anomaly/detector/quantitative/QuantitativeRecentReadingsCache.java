package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, Deque<TemperatureReading>> readingsMap = new ConcurrentHashMap<>();

    public QuantitativeRecentReadingsCache(@Value("${io.kontakt.anomaly.detector.quantitative.storage.threshold:10}")
                                           int threshold) {
        this.threshold = threshold;
    }

    @Override
    public synchronized List<TemperatureReading> push(TemperatureReading reading) {
        readingsMap.putIfAbsent(reading.roomId(), new ArrayDeque<>());
        Deque<TemperatureReading> roomReadings = readingsMap.get(reading.roomId());
        if (!roomReadings.isEmpty()) {
            if (roomReadings.size() >= threshold) {
                roomReadings.poll();
            }
        }
        roomReadings.add(reading);
        return roomReadings.stream().toList();
    }
}
