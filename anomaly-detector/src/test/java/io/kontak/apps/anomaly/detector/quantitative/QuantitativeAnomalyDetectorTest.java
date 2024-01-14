package io.kontak.apps.anomaly.detector.quantitative;

import io.kontak.apps.anomaly.detector.quantitative.QuantitativeAnomalyDetector;
import io.kontak.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontak.apps.anomaly.detector.quantitative.QuantitativeTempReadingsStorage;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;


public class QuantitativeAnomalyDetectorTest {

    private QuantitativeAnomalyDetector detector;

    @BeforeEach
    void setUp() {
        AnomaliesDatabaseService databaseService = Mockito.mock(AnomaliesDatabaseService.class);
        doNothing().when(databaseService).saveAnomaly(any());
        QuantitativeTempReadingsStorage storage = new QuantitativeTempReadingsStorage(5);
        detector = new QuantitativeAnomalyDetector(5, 5, storage, databaseService);

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