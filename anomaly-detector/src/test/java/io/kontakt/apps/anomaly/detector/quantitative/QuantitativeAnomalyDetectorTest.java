package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class QuantitativeAnomalyDetectorTest {

    private QuantitativeAnomalyDetector detector;

    @BeforeEach
    void setUp() {
        QuantitativeRecentReadingsCache storage = new QuantitativeRecentReadingsCache(5);
        detector = new QuantitativeAnomalyDetector(5, storage);

        TemperatureReading temperatureReading_1 = new TemperatureReading(20d, "room", "thermometer_1", Instant.parse("2023-01-01T00:00:00.000Z"));
        TemperatureReading temperatureReading_2 = new TemperatureReading(20d, "room", "thermometer_2", Instant.parse("2023-01-01T00:00:01.000Z"));
        TemperatureReading temperatureReading_3 = new TemperatureReading(20d, "room", "thermometer_3", Instant.parse("2023-01-01T00:00:02.000Z"));
        TemperatureReading temperatureReading_4 = new TemperatureReading(20d, "room", "thermometer_4", Instant.parse("2023-01-01T00:00:03.000Z"));

        storage.add(temperatureReading_1);
        storage.add(temperatureReading_2);
        storage.add(temperatureReading_3);
        storage.add(temperatureReading_4);
    }

    @Test
    public void should_detectAnomaly() {
        TemperatureReading temperatureReading_5 = new TemperatureReading(27d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));
        Optional<List<Anomaly>> anomaly = detector.apply(temperatureReading_5);

        assertTrue(anomaly.isPresent());
        assertEquals(temperatureReading_5.thermometerId(), anomaly.get().get(0).thermometerId());
    }

    @Test
    public void should_not_detectAnomaly() {
        TemperatureReading temperatureReading_5 = new TemperatureReading(22d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z"));
        Optional<List<Anomaly>> anomaly = detector.apply(temperatureReading_5);

        assertTrue(anomaly.isEmpty());
    }

    @Test
    public void should_not_detectDuplicateAnomaly() {
        TemperatureReading anomaly = new TemperatureReading(27d, "room", "anomaly", Instant.parse("2023-01-01T00:00:04.000Z"));

        Optional<List<Anomaly>> detectedAnomalies = detector.apply(anomaly);

        assertTrue(detectedAnomalies.isPresent());
        assertEquals(anomaly.thermometerId(), detectedAnomalies.get().get(0).thermometerId());

        detectedAnomalies = detector.apply(new TemperatureReading(22d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z")));
        assertTrue(detectedAnomalies.isEmpty());

        detectedAnomalies = detector.apply(new TemperatureReading(22d, "room", "thermometer_5", Instant.parse("2023-01-01T00:00:04.000Z")));
        assertTrue(detectedAnomalies.isEmpty());
    }
}