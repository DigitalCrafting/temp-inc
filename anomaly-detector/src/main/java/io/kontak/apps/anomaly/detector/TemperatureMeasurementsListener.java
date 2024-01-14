package io.kontak.apps.anomaly.detector;

import io.kontak.apps.anomaly.detector.archetype.AnomalyDetector;
import io.kontak.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontak.apps.event.Anomaly;
import io.kontak.apps.event.TemperatureReading;
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
