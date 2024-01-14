package io.kontakt.apps.analytics.api;

import io.kontakt.apps.anomaly.storage.AnomalyEntity;
import io.kontakt.apps.event.Anomaly;

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
