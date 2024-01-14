package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.anomaly.detector.archetype.RecentReadingsCache;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.List;

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

        storage.push(temperatureReading_1);
        List<TemperatureReading> actualReadings = storage.push(temperatureReading_2);

        Assertions.assertEquals(2, actualReadings.size());
        Assertions.assertEquals(temperatureReading_1.thermometerId(), actualReadings.get(0).thermometerId());
        Assertions.assertEquals(temperatureReading_2.thermometerId(), actualReadings.get(1).thermometerId());
    }

    @Test
    public void should_not_addMoreReadingsThatThreshold() {
        TemperatureReading temperatureReading_1 = new TemperatureReading(27d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(27d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading temperatureReading_3 = new TemperatureReading(27d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));
        TemperatureReading temperatureReading_4 = new TemperatureReading(27d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:03.000Z"));
        TemperatureReading temperatureReading_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));

        storage.push(temperatureReading_1);
        storage.push(temperatureReading_2);
        storage.push(temperatureReading_3);
        storage.push(temperatureReading_4);
        List<TemperatureReading> actualReadings = storage.push(temperatureReading_5);

        Assertions.assertEquals(4, actualReadings.size());
        Assertions.assertEquals(temperatureReading_2.thermometerId(), actualReadings.get(0).thermometerId());
        Assertions.assertEquals(temperatureReading_3.thermometerId(), actualReadings.get(1).thermometerId());
        Assertions.assertEquals(temperatureReading_4.thermometerId(), actualReadings.get(2).thermometerId());
        Assertions.assertEquals(temperatureReading_5.thermometerId(), actualReadings.get(3).thermometerId());
    }

}