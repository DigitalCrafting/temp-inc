package io.kontakt.apps.anomaly.detector.storage;

import io.kontakt.apps.event.TemperatureReading;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

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
