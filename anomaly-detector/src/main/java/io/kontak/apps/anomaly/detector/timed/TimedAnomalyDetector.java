package io.kontak.apps.anomaly.detector.timed;

import io.kontak.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontak.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontak.apps.anomaly.detector.utils.AnomalyDetectorUtils;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TimedAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final TimedTempReadingsStorage storage;
    private final AnomaliesDatabaseService databaseService;

    public TimedAnomalyDetector(
            @Value("${io.kontak.anomaly.detector.timed.temperatureDifference.threshold:5}")
            double tempDiffThreshold, TimedTempReadingsStorage storage, AnomaliesDatabaseService databaseService) {
        this.tempDiffThreshold = tempDiffThreshold;
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
            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, roomReadings, tempDiffThreshold);
            potentialAnomaly.ifPresent(anomalies::add);
        }
        if (!anomalies.isEmpty()) {
            anomalies.forEach(databaseService::saveAnomaly);
            return Optional.of(anomalies.get(0));
        }
        return Optional.empty();
    }
}
