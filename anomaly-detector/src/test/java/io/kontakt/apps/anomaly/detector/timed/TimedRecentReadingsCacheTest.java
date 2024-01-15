package io.kontakt.apps.anomaly.detector.timed;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimedRecentReadingsCacheTest {
    private RecentReadingsCache storage;

    @BeforeEach
    public void setUp() {
        storage = new TimedRecentReadingsCache(2);
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
        storage.add(temperatureReading_5);

        List<TemperatureReading> actualReadings = storage.get();

        assertEquals(3, actualReadings.size());
        assertEquals(temperatureReading_3.thermometerId(), actualReadings.get(0).thermometerId());
        assertEquals(temperatureReading_4.thermometerId(), actualReadings.get(1).thermometerId());
        assertEquals(temperatureReading_5.thermometerId(), actualReadings.get(2).thermometerId());
    }

    @Test
    public void should_correctlyRemoveMultiplePreviousReadings() {
        storage = new TimedRecentReadingsCache(10);
        /* Expected to be evicted by the end */
        TemperatureReading temperatureReading_1 = new TemperatureReading(27d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(27d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading temperatureReading_3 = new TemperatureReading(27d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));

        /* Expected to remain */
        TemperatureReading temperatureReading_4 = new TemperatureReading(27d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:08.000Z"));
        TemperatureReading temperatureReading_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:09.000Z"));

        /* Eviction triggering reading */
        TemperatureReading temperatureReading_6 = new TemperatureReading(27d, "room", "thermometer_6", Instant.parse("2023-01-01T00:00:15.000Z"));

        /* Initial storage population */
        storage.add(temperatureReading_1);
        storage.add(temperatureReading_2);
        storage.add(temperatureReading_3);
        storage.add(temperatureReading_4);
        Optional<Set<TemperatureReading>> intermediateEvicted = storage.add(temperatureReading_5);

        List<TemperatureReading> intermediateActualReadings = storage.get();

        /* Intermediate assertion */

        assertTrue(intermediateEvicted.isEmpty());
        assertEquals(5, intermediateActualReadings.size());
        assertTrue(intermediateActualReadings.contains(temperatureReading_1));
        assertTrue(intermediateActualReadings.contains(temperatureReading_2));
        assertTrue(intermediateActualReadings.contains(temperatureReading_3));
        assertTrue(intermediateActualReadings.contains(temperatureReading_4));
        assertTrue(intermediateActualReadings.contains(temperatureReading_5));

        /* Eviction trigger */
        Optional<Set<TemperatureReading>> evicted = storage.add(temperatureReading_6);
        List<TemperatureReading> actualReadings = storage.get();

        /* Final assertions */
        assertTrue(evicted.isPresent());
        assertEquals(3, evicted.get().size());
        assertTrue(evicted.get().contains(temperatureReading_1));
        assertTrue(evicted.get().contains(temperatureReading_2));
        assertTrue(evicted.get().contains(temperatureReading_3));


        assertEquals(3, actualReadings.size());
        assertTrue(actualReadings.contains(temperatureReading_4));
        assertTrue(actualReadings.contains(temperatureReading_5));
        assertTrue(actualReadings.contains(temperatureReading_6));
    }
}