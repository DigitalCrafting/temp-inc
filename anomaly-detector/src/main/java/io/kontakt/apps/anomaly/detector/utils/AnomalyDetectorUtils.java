package io.kontakt.apps.anomaly.detector.utils;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;

import java.util.List;
import java.util.Optional;

public final class AnomalyDetectorUtils {
    private AnomalyDetectorUtils() {
    }

    public static Optional<Anomaly> isAnomaly(TemperatureReading current, List<TemperatureReading> otherReadings, double tempDiffThreshold) {
        double average = otherReadings.stream()
                .map(TemperatureReading::temperature)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        if (Math.abs(average - current.temperature()) > tempDiffThreshold) {
            return Optional.of(new Anomaly(
                    current.temperature(),
                    current.roomId(),
                    current.thermometerId(),
                    current.timestamp()));
        }
        return Optional.empty();
    }
}
