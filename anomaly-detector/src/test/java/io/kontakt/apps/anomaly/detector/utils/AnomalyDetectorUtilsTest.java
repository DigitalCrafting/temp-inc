package io.kontakt.apps.anomaly.detector.utils;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnomalyDetectorUtilsTest {
    private List<TemperatureReading> readings;

    @Test
    void should_detectAnomalyWithGreaterTemp_forPositiveAverage() {
        setUpPositiveAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(27d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_detectAnomalyWithLowerTemp_forPositiveAverage() {
        setUpPositiveAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(10d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_detectAnomalyWithNegativeTemp_forPositiveAverage() {
        setUpPositiveAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(-19d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_not_detectAnomaly_forPositiveAverage() {
        setUpPositiveAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(19d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isEmpty());
    }

    @Test
    void should_detectAnomalyWithGreaterTemp_forNegativeAverage() {
        setUpNegativeAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(-12d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_detectAnomalyWithLowerTemp_forNegativeAverage() {
        setUpNegativeAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(-29d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_detectAnomalyWithPositiveTemp_forNegativeAverage() {
        setUpNegativeAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(19d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isPresent());
        assertEquals(expectedAnomaly.temperature(), actualAnomaly.get().temperature());
    }

    @Test
    void should_not_detectAnomaly_forNegativeAverage() {
        setUpNegativeAverage();
        TemperatureReading expectedAnomaly = new TemperatureReading(-19d, "room", "thermometer5", Instant.now());

        Optional<Anomaly> actualAnomaly = AnomalyDetectorUtils.isAnomaly(expectedAnomaly, readings, 5);

        assertTrue(actualAnomaly.isEmpty());
    }

    private void setUpPositiveAverage() {
        readings = List.of(
                new TemperatureReading(21d, "room", "thermometer", Instant.now()),
                new TemperatureReading(20d, "room", "thermometer", Instant.now()),
                new TemperatureReading(20d, "room", "thermometer", Instant.now()),
                new TemperatureReading(19d, "room", "thermometer", Instant.now()),
                new TemperatureReading(20d, "room", "thermometer", Instant.now())
        );
    }

    private void setUpNegativeAverage() {
        readings = List.of(
                new TemperatureReading(-21d, "room", "thermometer", Instant.now()),
                new TemperatureReading(-20d, "room", "thermometer", Instant.now()),
                new TemperatureReading(-20d, "room", "thermometer", Instant.now()),
                new TemperatureReading(-19d, "room", "thermometer", Instant.now()),
                new TemperatureReading(-20d, "room", "thermometer", Instant.now())
        );
    }
}