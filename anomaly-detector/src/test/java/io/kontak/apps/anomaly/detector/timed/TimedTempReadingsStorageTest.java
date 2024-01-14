package io.kontak.apps.anomaly.detector.timed;

import io.kontak.apps.anomaly.detector.archetype.TempReadingsStorage;
import io.kontak.apps.anomaly.detector.timed.TimedTempReadingsStorage;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.util.List;

public class TimedTempReadingsStorageTest {
    private TempReadingsStorage storage;

    @BeforeEach
    public void setUp() {
        storage = new TimedTempReadingsStorage(2);
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

        Assertions.assertEquals(3, actualReadings.size());
        Assertions.assertEquals(temperatureReading_3.thermometerId(), actualReadings.get(0).thermometerId());
        Assertions.assertEquals(temperatureReading_4.thermometerId(), actualReadings.get(1).thermometerId());
        Assertions.assertEquals(temperatureReading_5.thermometerId(), actualReadings.get(2).thermometerId());
    }

    @Test
    public void should_correctlyRemoveMultiplePreviousReadings() {
        storage = new TimedTempReadingsStorage(10);
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
        storage.push(temperatureReading_1);
        storage.push(temperatureReading_2);
        storage.push(temperatureReading_3);
        storage.push(temperatureReading_4);
        List<TemperatureReading> intermediateActualReadings = storage.push(temperatureReading_5);

        /* Intermediate assertion */
        Assertions.assertEquals(5, intermediateActualReadings.size());
        Assertions.assertEquals(temperatureReading_1.thermometerId(), intermediateActualReadings.get(0).thermometerId());
        Assertions.assertEquals(temperatureReading_2.thermometerId(), intermediateActualReadings.get(1).thermometerId());
        Assertions.assertEquals(temperatureReading_3.thermometerId(), intermediateActualReadings.get(2).thermometerId());
        Assertions.assertEquals(temperatureReading_4.thermometerId(), intermediateActualReadings.get(3).thermometerId());
        Assertions.assertEquals(temperatureReading_5.thermometerId(), intermediateActualReadings.get(4).thermometerId());


        /* Eviction trigger */
        List<TemperatureReading> actualReadings = storage.push(temperatureReading_6);

        /* Final assertions */
        Assertions.assertEquals(3, actualReadings.size());
        Assertions.assertEquals(temperatureReading_4.thermometerId(), actualReadings.get(0).thermometerId());
        Assertions.assertEquals(temperatureReading_5.thermometerId(), actualReadings.get(1).thermometerId());
        Assertions.assertEquals(temperatureReading_6.thermometerId(), actualReadings.get(2).thermometerId());
    }
}