package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontakt.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.apache.kafka.streams.kstream.KStream;

import java.util.function.Function;

public class TemperatureMeasurementsListener implements Function<KStream<String, TemperatureReading>, KStream<String, Anomaly>> {

    private final AnomalyDetector anomalyDetector;
    private final AnomaliesDatabaseService databaseService;

    public TemperatureMeasurementsListener(AnomalyDetector anomalyDetector, AnomaliesDatabaseService databaseService) {
        this.anomalyDetector = anomalyDetector;
        this.databaseService = databaseService;
    }

    @Override
    public KStream<String, Anomaly> apply(KStream<String, TemperatureReading> events) {
        return events
                .mapValues(anomalyDetector::apply)
                .filter((s, anomaly) -> anomaly.isPresent())
                .mapValues((s, anomaly) -> anomaly.get())
                .peek((s, anomaly) -> databaseService.saveAnomaly(anomaly))
                .selectKey((s, anomaly) -> anomaly.thermometerId());
    }
}
