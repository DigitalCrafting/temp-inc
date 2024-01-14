package io.kontak.apps.anomaly.detector;

import io.kontak.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("default")
public class AlwaysAnomalyAnomalyDetector implements AnomalyDetector {
    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {

        TemperatureReading temperatureReading = temperatureReadings.get(0);
        return Optional.of(new Anomaly(
                temperatureReading.temperature(),
                temperatureReading.roomId(),
                temperatureReading.thermometerId(),
                temperatureReading.timestamp()
        ));
    }
}
