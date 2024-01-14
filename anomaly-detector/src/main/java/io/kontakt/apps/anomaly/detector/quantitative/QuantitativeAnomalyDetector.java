package io.kontakt.apps.anomaly.detector.quantitative;

import io.kontakt.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontakt.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontakt.apps.anomaly.detector.utils.AnomalyDetectorUtils;
import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@Component
@Profile("quantitative")
public class QuantitativeAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final int storageThreshold;
    private final QuantitativeTempReadingsStorage storage;

    public QuantitativeAnomalyDetector(
            @Value("${io.kontakt.anomaly.detector.quantitative.temperatureDifference.threshold:5}")
            double tempDiffThreshold,
            @Value("${io.kontakt.anomaly.detector.quantitative.storage.threshold:10}")
            int storageThreshold,
            QuantitativeTempReadingsStorage storage,
            AnomaliesDatabaseService databaseService) {
        this.tempDiffThreshold = tempDiffThreshold;
        this.storageThreshold = storageThreshold;
        this.storage = storage;
    }

    @Override
    public Optional<Anomaly> apply(TemperatureReading reading) {
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
            log.info("[Quantitative detector] Processing reading: " + current);
            List<TemperatureReading> otherReadings = roomReadings.stream().filter(
                    r -> !r.equals(current)
            ).toList();

            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, otherReadings, tempDiffThreshold);
            potentialAnomaly.ifPresent(anomalies::add);
        }
        if (!anomalies.isEmpty()) {
            return Optional.of(anomalies.get(0));
        }
        return Optional.empty();
    }
}
