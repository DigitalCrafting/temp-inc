package io.kontakt.apps.anomaly.detector.timed;

import io.kontakt.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontakt.apps.anomaly.detector.storage.DetectedAnomaliesCache;
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
import java.util.Set;

@Log
@Component
@Profile("timed")
public class TimedAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final TimedRecentReadingsCache recentReadingsCache;
    private final DetectedAnomaliesCache detectedAnomaliesCache;

    public TimedAnomalyDetector(
            @Value("${io.kontakt.anomaly.detector.timed.temperatureDifference.threshold:5}")
            double tempDiffThreshold, TimedRecentReadingsCache recentReadingsCache) {
        this.tempDiffThreshold = tempDiffThreshold;
        this.recentReadingsCache = recentReadingsCache;
        this.detectedAnomaliesCache = new DetectedAnomaliesCache();
    }

    @Override
    public Optional<List<Anomaly>> apply(TemperatureReading reading) {
        Optional<Set<TemperatureReading>> evicted = recentReadingsCache.add(reading);
        detectedAnomaliesCache.evict(evicted);
        List<TemperatureReading> recentReadings = recentReadingsCache.get();
        return detectAnomalies(recentReadings);
    }

    /**
     * Define an anomaly as any measurement that differs from the average of all readings by 5 degrees Celsius
     * within a 10-second window based on the measurement timestamp.
     * <p>
     * recentReadingsCache takes care of the time window.
     * <p>
     * detectedAnomaliesCache keeps track of already detected anomalies so that we do not detect something twice
     * <p>
     * This time we do not have size constraint but there is no point in measuring average of less than 3 elements,
     * so I'm using 3 as a minimum size. Depending on the requirements, this can be adjusted.
     * <p>
     * We could, for example, check if the time difference between first and last reading is 10s,
     * but it could be *almost* 10s and since this is recruitment task, I'm going to simplify things a bit.
     */
    private Optional<List<Anomaly>> detectAnomalies(List<TemperatureReading> recentReadings) {
        if (recentReadings.size() < 3) {
            return Optional.empty();
        }

        List<Anomaly> anomalies = new ArrayList<>();
        for (TemperatureReading current : recentReadings) {
            if (detectedAnomaliesCache.contains(current)) {
                continue;
            }

            log.info("[Timed detector] Processing reading: " + current);
            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, recentReadings, tempDiffThreshold);
            if (potentialAnomaly.isPresent()) {
                anomalies.add(potentialAnomaly.get());
                detectedAnomaliesCache.add(current);
            }
        }

        if (!anomalies.isEmpty()) {
            return Optional.of(anomalies);
        }
        return Optional.empty();
    }
}
