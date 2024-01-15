package io.kontakt.apps.anomaly.detector.quantitative;

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
@Profile("quantitative")
public class QuantitativeAnomalyDetector implements AnomalyDetector {

    private final double tempDiffThreshold;
    private final QuantitativeRecentReadingsCache recentReadingsCache;
    private final DetectedAnomaliesCache detectedAnomaliesCache;

    public QuantitativeAnomalyDetector(
            @Value("${io.kontakt.anomaly.detector.quantitative.temperatureDifference.threshold:5}")
            double tempDiffThreshold,
            QuantitativeRecentReadingsCache recentReadingsCache) {
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
     * within any 10 consecutive measurements, one reading
     * is higher than the average of the remaining 9 by 5 degrees Celsius.
     * <p>
     * recentReadingsCache takes care of the time window.
     * <p>
     * detectedAnomaliesCache keeps track of already detected anomalies so that we do not detect something twice
     * <p>
     * There is no point in measuring average of less than 3 elements.
     * <p>
     * There will never be more than 10, storage takes care of that.
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

            log.info("[Quantitative detector] Processing reading: " + current);
            List<TemperatureReading> otherReadings = recentReadings.stream().filter(
                    r -> !r.equals(current)
            ).toList();

            Optional<Anomaly> potentialAnomaly = AnomalyDetectorUtils.isAnomaly(current, otherReadings, tempDiffThreshold);
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
