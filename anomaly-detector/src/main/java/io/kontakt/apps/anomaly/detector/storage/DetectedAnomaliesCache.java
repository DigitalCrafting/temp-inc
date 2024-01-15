package io.kontakt.apps.anomaly.detector.storage;

import io.kontakt.apps.event.TemperatureReading;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This is a helper data structure that stores readings with anomalies.
 * It helps with preventing anomalies being reported multiple times,
 * since the algorithm analyzes the readings range multiple times
 * in order to account for changes in the average based on new readings.
 * <p>
 * Probably the simpler solution would be to just add a boolean value to {@link TemperatureReading},
 * something like 'alreadyDetected', but it would mean introducing detector specific property
 * into the domain object which can be shared between many modules,
 * which would violate separation of concerns for these modules.
 */
public class DetectedAnomaliesCache {
    private final Set<TemperatureReading> detectedAnomalies;

    public DetectedAnomaliesCache() {
        this.detectedAnomalies = new LinkedHashSet<>();
    }

    public void add(TemperatureReading anomaly) {
        detectedAnomalies.add(anomaly);
    }

    public boolean contains(TemperatureReading anomaly) {
        return detectedAnomalies.contains(anomaly);
    }

    public void evict(Optional<Set<TemperatureReading>> anomaliesOptional) {
        anomaliesOptional.ifPresent(this::evict);
    }

    public void evict(Set<TemperatureReading> anomalies) {
        this.detectedAnomalies.removeAll(anomalies);
    }

    public void evict(TemperatureReading anomaly) {
        detectedAnomalies.remove(anomaly);
    }
}
