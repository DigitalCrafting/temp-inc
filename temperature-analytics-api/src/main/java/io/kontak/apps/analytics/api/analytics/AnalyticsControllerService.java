package io.kontak.apps.analytics.api.analytics;

import io.kontak.apps.event.Anomaly;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
class AnalyticsControllerService {
    List<Anomaly> getAnomaliesPerRoom(@PathVariable("roomId") final String roomId) {
        return null;
    }

    List<Anomaly> getAnomaliesPerThermometer(@PathVariable("thermometerId") final String thermometerId) {
        return null;
    }
}
