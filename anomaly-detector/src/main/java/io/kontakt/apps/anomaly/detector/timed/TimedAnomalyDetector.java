package io.kontakt.apps.anomaly.detector.timed;

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
@Profile("timed")
public class TimedAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final TimedRecentReadingsCache storage;

    public TimedAnomalyDetector(
            @Value("${io.kontakt.anomaly.detector.timed.temperatureDifference.threshold:5}")
            double tempDiffThreshold, TimedRecentReadingsCache storage, AnomaliesDatabaseService databaseService) {
        this.tempDiffThreshold = tempDiffThreshold;
        this.storage = storage;
    }

    @Override
    public Optional<Anomaly> apply(TemperatureReading reading) {
        List<TemperatureReading> roomReadings = storage.push(reading);
        return detectAnomalies(roomReadings);
    }

    /**
     * Define an anomaly as any measurement that differs from the average of all readings by 5 degrees Celsius
     * within a 10-second window based on the measurement timestamp.
     * <p>
     * Storage takes care of the time window.
     * <p>
     * This time we do not have size constraint but there is no point in measuring average of less than 5 elements,
     * so I'm using 5 as a minimum size. Depending on the requirements, this can be adjusted.
     * <p>
     * We could, for example, check if the time difference between first and last reading is 10s,
     * but it could be *almost* 10s and since this is recruitment task, I'm going to simplify things a bit.
     */
    private Optional<Anomaly> detectAnomalies(List<TemperatureReading> roomReadings) {
        if (roomReadings.size() < 5) {
            return Optional.empty();
        }

        List<Anomaly> anomalies = new ArrayList<>();
        for (TemperatureReading current : roomReadings) {
            log.info("[Timed detector] Processing reading: " + current);
            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, roomReadings, tempDiffThreshold);
            potentialAnomaly.ifPresent(anomalies::add);
        }
        if (!anomalies.isEmpty()) {
            return Optional.of(anomalies.get(0));
        }
        return Optional.empty();
    }
}
