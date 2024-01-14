package io.kontak.apps.anomaly.detector.archetype;

import io.kontak.apps.event.TemperatureReading;

import java.util.List;

/**
 * Temporary storage interface.
 *
 * The requirements for detector only require us to store detected anomalies,
 * hence there is no point in preserving all the reading in this application,
 * they will only be stored until the application is running.
 * */
public interface TempReadingsStorage {
    List<TemperatureReading> push(TemperatureReading reading);
}
