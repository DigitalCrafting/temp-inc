package io.kontak.apps.anomaly.detector.impl;

import io.kontak.apps.anomaly.detector.tempStorage.QuantitativeTempReadingsStorage;
import io.kontak.apps.anomaly.detector.tempStorage.TimedTempReadingsStorage;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TimedAnomalyDetectorTest {

    private TimedAnomalyDetector detector;

    @BeforeEach
    void setUp() {
        TimedTempReadingsStorage storage = new TimedTempReadingsStorage(5);
        detector = new TimedAnomalyDetector(5, storage);

        TemperatureReading temperatureReading_1 = new TemperatureReading(20d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(20d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading temperatureReading_3 = new TemperatureReading(20d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));
        TemperatureReading temperatureReading_4 = new TemperatureReading(20d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:03.000Z"));

        storage.push(temperatureReading_1);
        storage.push(temperatureReading_2);
        storage.push(temperatureReading_3);
        storage.push(temperatureReading_4);
    }

    @Test
    public void should_detectAnomaly() {
        TemperatureReading temperatureReading_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));
        Optional<Anomaly> anomaly = detector.apply(List.of(temperatureReading_5));

        assertTrue(anomaly.isPresent());
        assertEquals(temperatureReading_5.thermometerId(), anomaly.get().thermometerId());
    }

    @Test
    public void should_not_detectAnomaly() {
        TemperatureReading temperatureReading_5 = new TemperatureReading(22d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));
        Optional<Anomaly> anomaly = detector.apply(List.of(temperatureReading_5));

        assertTrue(anomaly.isEmpty());
    }
}