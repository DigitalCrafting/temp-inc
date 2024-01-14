package io.kontak.apps.analytics.api;

import io.kontak.apps.anomaly.storage.AnomaliesMapper;
import io.kontak.apps.event.Anomaly;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
class AnalyticsControllerService {
    private final AnomaliesMapper mapper;

    public AnalyticsControllerService(AnomaliesMapper mapper) {
        this.mapper = mapper;
    }

    List<Anomaly> getAnomaliesPerRoom(@PathVariable("roomId") final String roomId) {
        List<Anomaly> anomalies = mapper.getByRoomId(roomId).stream()
                .map(AnomaliesConverter::convert)
                .toList();
        return anomalies;
    }

    List<Anomaly> getAnomaliesPerThermometer(@PathVariable("thermometerId") final String thermometerId) {
        List<Anomaly> anomalies = mapper.getByThermometerId(thermometerId).stream()
                .map(AnomaliesConverter::convert)
                .toList();
        return anomalies;
    }

    public List<String> getThermometerOverAnomalyThreshold(Integer threshold) {
        return mapper.getThermometersWithAnomaliesAboveThreshold(threshold);
    }

    public List<String> getRoomsWithAnomalies() {
        return mapper.getRoomsWithAnomalies();
    }

    public List<String> getThermometersForRoom(String roomId) {
        return mapper.getThermometersForRoom(roomId);
    }
}
