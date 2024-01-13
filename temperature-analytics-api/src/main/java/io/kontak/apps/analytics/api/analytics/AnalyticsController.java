package io.kontak.apps.analytics.api.analytics;

import io.kontak.apps.event.Anomaly;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsControllerService service;

    public AnalyticsController(AnalyticsControllerService service) {
        this.service = service;
    }

    @GetMapping("/rooms/{roomId}/anomalies")
    public List<Anomaly> getAnomaliesPerRoom(@PathVariable("roomId") final String roomId) {
        return service.getAnomaliesPerRoom(roomId);
    }

    @GetMapping("/thermometers/{thermometerId}/anomalies")
    public List<Anomaly> getAnomaliesPerThermometer(@PathVariable("thermometerId") final String thermometerId) {
        return service.getAnomaliesPerThermometer(thermometerId);
    }

}
