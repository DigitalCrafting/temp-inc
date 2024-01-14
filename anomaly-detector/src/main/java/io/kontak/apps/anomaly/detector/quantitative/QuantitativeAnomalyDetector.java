package io.kontak.apps.anomaly.detector.quantitative;

import io.kontak.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontak.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontak.apps.anomaly.detector.utils.AnomalyDetectorUtils;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Profile("quantitative")
public class QuantitativeAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final int storageThreshold;
    private final QuantitativeTempReadingsStorage storage;
    private final AnomaliesDatabaseService databaseService;

    public QuantitativeAnomalyDetector(
            @Value("${io.kontak.anomaly.detector.quantitative.temperatureDifference.threshold:5}")
            double tempDiffThreshold,
            @Value("${io.kontak.anomaly.detector.quantitative.storage.threshold:10}")
            int storageThreshold,
            QuantitativeTempReadingsStorage storage,
            AnomaliesDatabaseService databaseService) {
        this.tempDiffThreshold = tempDiffThreshold;
        this.storageThreshold = storageThreshold;
        this.storage = storage;
        this.databaseService = databaseService;
    }

    @Override
    public Optional<Anomaly> apply(List<TemperatureReading> temperatureReadings) {
        TemperatureReading reading = temperatureReadings.get(0);
        List<TemperatureReading> roomReadings = storage.push(reading);
        return detectAnomalies(roomReadings);
    }

    /**
     * within any 10 consecutive measurements, one reading
     * is higher than the average of the remaining 9 by 5 degrees Celsius.
     * <p>
     * When there is less than 10 readings, we do nothing.
     * <p>
     * There will never be more than 10, storage takes care of that.
     */
    private Optional<Anomaly> detectAnomalies(List<TemperatureReading> roomReadings) {
        if (roomReadings.size() < storageThreshold) {
            return Optional.empty();
        }

        List<Anomaly> anomalies = new ArrayList<>();
        for (TemperatureReading current : roomReadings) {
            List<TemperatureReading> otherReadings = roomReadings.stream().filter(
                    r -> !r.equals(current)
            ).toList();

            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, otherReadings, tempDiffThreshold);
            potentialAnomaly.ifPresent(anomalies::add);
        }
        if (!anomalies.isEmpty()) {
            anomalies.forEach(databaseService::saveAnomaly);
            return Optional.of(anomalies.get(0));
        }
        return Optional.empty();
    }
}
