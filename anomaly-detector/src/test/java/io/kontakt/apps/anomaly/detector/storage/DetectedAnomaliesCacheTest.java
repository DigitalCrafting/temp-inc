package io.kontakt.apps.anomaly.detector.storage;

import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DetectedAnomaliesCacheTest {
    private DetectedAnomaliesCache cache;

    @BeforeEach
    void setUp() {
        cache = new DetectedAnomaliesCache();
    }

    @Test
    void should_correctlyEvictAnomalies() {
        TemperatureReading anomaly_1 = new TemperatureReading(27d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading anomaly_2 = new TemperatureReading(27d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading anomaly_3 = new TemperatureReading(27d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));
        TemperatureReading anomaly_4 = new TemperatureReading(27d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:03.000Z"));
        TemperatureReading anomaly_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));

        cache.add(anomaly_1);
        cache.add(anomaly_2);
        cache.add(anomaly_3);
        cache.add(anomaly_4);
        cache.add(anomaly_5);

        cache.evict(Set.of(anomaly_1, anomaly_3, anomaly_5));

        assertTrue(cache.contains(anomaly_2));
        assertTrue(cache.contains(anomaly_4));
        assertFalse(cache.contains(anomaly_1));
        assertFalse(cache.contains(anomaly_3));
        assertFalse(cache.contains(anomaly_5));
    }
}