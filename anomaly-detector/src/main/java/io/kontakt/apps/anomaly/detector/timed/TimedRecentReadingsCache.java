package io.kontakt.apps.anomaly.detector.timed;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
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
public class TimedRecentReadingsCache implements RecentReadingsCache {

    private final long threshold;
    private final Map<String, Deque<TemperatureReading>> readingsMap = new ConcurrentHashMap<>();

    public TimedRecentReadingsCache(@Value("${io.kontakt.anomaly.detector.timed.storage.threshold:10}")
                                    long threshold) {
        this.threshold = threshold;
    }

    @Override
    public synchronized List<TemperatureReading> push(TemperatureReading reading) {
        readingsMap.putIfAbsent(reading.roomId(), new ArrayDeque<>());
        Deque<TemperatureReading> roomReadings = readingsMap.get(reading.roomId());
        if (!roomReadings.isEmpty()) {
            /* We take into account that there can be more readings to poll from queue */
            TemperatureReading headReading = roomReadings.peekFirst();
            while (roomReadings.peekFirst() != null && Duration.between(headReading.timestamp(), reading.timestamp()).getSeconds() > threshold) {
                roomReadings.pollFirst();
                headReading = roomReadings.peekFirst();
            }
        }
        roomReadings.add(reading);
        return roomReadings.stream().toList();
    }
}
