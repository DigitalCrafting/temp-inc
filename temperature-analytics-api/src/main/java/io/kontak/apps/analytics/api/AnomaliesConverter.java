package io.kontak.apps.analytics.api;

import io.kontak.apps.anomaly.storage.AnomalyEntity;
import io.kontak.apps.event.Anomaly;

public class AnomaliesConverter {
    public static Anomaly convert(AnomalyEntity entity) {
        return new Anomaly(
                entity.getTemperature(),
                entity.getRoomId(),
                entity.getThermometerId(),
                entity.getTimestamp()
        );
    }
}
