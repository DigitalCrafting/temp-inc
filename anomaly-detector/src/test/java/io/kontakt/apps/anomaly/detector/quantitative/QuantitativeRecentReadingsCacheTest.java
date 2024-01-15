package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
public class QuantitativeRecentReadingsCacheTest {

    private RecentReadingsCache storage;

    @BeforeEach
    public void setUp() {
        storage = new QuantitativeRecentReadingsCache(4);
    }

    @Test
    public void should_correctlyAddReadings() {
        TemperatureReading temperatureReading_1 = new TemperatureReading(27d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(27d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:00.000Z"));

        storage.add(temperatureReading_1);
        storage.add(temperatureReading_2);
        List<TemperatureReading> actualReadings = storage.get();

        assertEquals(2, actualReadings.size());
        assertEquals(temperatureReading_1.thermometerId(), actualReadings.get(0).thermometerId());
        assertEquals(temperatureReading_2.thermometerId(), actualReadings.get(1).thermometerId());
    }

    @Test
    public void should_not_addMoreReadingsThatThreshold() {
        TemperatureReading temperatureReading_1 = new TemperatureReading(27d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(27d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading temperatureReading_3 = new TemperatureReading(27d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));
        TemperatureReading temperatureReading_4 = new TemperatureReading(27d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:03.000Z"));
        TemperatureReading temperatureReading_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));

        storage.add(temperatureReading_1);
        storage.add(temperatureReading_2);
        storage.add(temperatureReading_3);
        storage.add(temperatureReading_4);
        Optional<Set<TemperatureReading>> evicted = storage.add(temperatureReading_5);
        List<TemperatureReading> actualReadings = storage.get();

        assertEquals(4, actualReadings.size());
        assertEquals(temperatureReading_2.thermometerId(), actualReadings.get(0).thermometerId());
        assertEquals(temperatureReading_3.thermometerId(), actualReadings.get(1).thermometerId());
        assertEquals(temperatureReading_4.thermometerId(), actualReadings.get(2).thermometerId());
        assertEquals(temperatureReading_5.thermometerId(), actualReadings.get(3).thermometerId());

        assertTrue(evicted.isPresent());
        assertFalse(evicted.get().isEmpty());
        assertTrue(evicted.get().contains(temperatureReading_1));
    }

}