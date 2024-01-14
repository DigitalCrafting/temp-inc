package io.kontak.apps.anomaly.detector.timed;

import io.kontak.apps.anomaly.detector.archetype.TempReadingsStorage;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Temporary storage for timed readings.
 * <p>
 * We have 2 storages, because timed and quantitative readings have different criteria
 * for how many if them we need.
 * <p>
 * This storage is basically an LRU cache, that will evict readings
 * no longer satisfying the criteria for storage.
 */
@Component
public class TimedTempReadingsStorage implements TempReadingsStorage {

    private final long threshold;
    private final Map<String, Deque<TemperatureReading>> readingsMap = new ConcurrentHashMap<>();

    public TimedTempReadingsStorage(@Value("${io.kontak.anomaly.detector.timed.storage.threshold:10}")
                                    long threshold) {
        this.threshold = threshold;
    }

    @Override
    public synchronized List<TemperatureReading> push(TemperatureReading reading) {
        readingsMap.putIfAbsent(reading.roomId(), new ArrayDeque<>());
        Deque<TemperatureReading> roomReadings = readingsMap.get(reading.roomId());
        if (!roomReadings.isEmpty()) {
            while (true) {
                TemperatureReading headReading = roomReadings.peekFirst();
                if (Duration.between(headReading.timestamp(), reading.timestamp()).getSeconds() > threshold) {
                    roomReadings.pollFirst();
                } else {
                    break;
                }
            }
        }
        roomReadings.add(reading);
        return roomReadings.stream().toList();
    }
}
