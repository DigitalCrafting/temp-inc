package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("default")
public class AlwaysAnomalyDetector implements AnomalyDetector {
    @Override
    public Optional<Anomaly> apply(TemperatureReading temperatureReading) {
        return Optional.of(new Anomaly(
                temperatureReading.temperature(),
                temperatureReading.roomId(),
                temperatureReading.thermometerId(),
                temperatureReading.timestamp()
        ));
    }
}
