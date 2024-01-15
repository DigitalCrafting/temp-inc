package io.kontakt.apps.anomaly.detector.archetype;

import io.kontakt.apps.event.TemperatureReading;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Temporary storage interface.
 * <p>
 * The requirements for detector only require us to store detected anomalies,
 * hence there is no point in preserving all the reading in this application,
 * they will only be stored until the application is running.
 */
public interface RecentReadingsCache {
    /**
     * Adds a new reading to cache.
     * Evicts readings no longer satisfying the condition.
     *
     * @param reading {@link TemperatureReading} to be added to cache.
     * @return {@link Optional} set of evicted {@link TemperatureReading} elements.
     */
    Optional<Set<TemperatureReading>> add(TemperatureReading reading);

    /**
     * @return list of recent {@link TemperatureReading}
     */
    List<TemperatureReading> get();
}
