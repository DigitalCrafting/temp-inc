package io.kontakt.apps.anomaly.detector.archetype;

import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;

import java.util.Optional;
import java.util.function.Function;

public interface AnomalyDetector extends Function<TemperatureReading, Optional<Anomaly>> {

}
