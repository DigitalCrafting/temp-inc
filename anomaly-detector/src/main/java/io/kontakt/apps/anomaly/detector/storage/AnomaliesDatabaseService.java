package io.kontakt.apps.anomaly.detector.storage;

import io.kontakt.apps.anomaly.storage.AnomaliesMapper;
import io.kontakt.apps.anomaly.storage.AnomalyEntity;
import io.kontakt.apps.event.Anomaly;
import org.springframework.stereotype.Service;

@Service
public class AnomaliesDatabaseService {
    private final AnomaliesMapper mapper;

    public AnomaliesDatabaseService(AnomaliesMapper mapper) {
        this.mapper = mapper;
    }

    public void saveAnomaly(Anomaly anomaly) {
        AnomalyEntity entity = new AnomalyEntity();
        entity.setTemperature(anomaly.temperature());
        entity.setRoomId(anomaly.roomId());
        entity.setThermometerId(anomaly.thermometerId());
        entity.setTimestamp(anomaly.timestamp());

        mapper.insert(entity);
    }
}
