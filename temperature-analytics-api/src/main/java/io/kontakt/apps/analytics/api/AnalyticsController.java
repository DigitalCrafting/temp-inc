package io.kontakt.apps.analytics.api;

import io.kontakt.apps.event.Anomaly;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analytics/anomalies")
public class AnalyticsController {
    private final AnalyticsControllerService service;

    public AnalyticsController(AnalyticsControllerService service) {
        this.service = service;
    }

    @ResponseBody
    @GetMapping("/rooms")
    public List<String> getRoomsWithAnomalies() {
        return service.getRoomsWithAnomalies();
    }

    @ResponseBody
    @GetMapping("/rooms/{roomId}")
    public List<Anomaly> getAnomaliesPerRoom(@PathVariable("roomId") final String roomId) {
        return service.getAnomaliesPerRoom(roomId);
    }

    @ResponseBody
    @GetMapping("/rooms/{roomId}/thermometers")
    public List<String> getThermometersForRoom(@PathVariable("roomId") final String roomId) {
        return service.getThermometersForRoom(roomId);
    }

    @ResponseBody
    @GetMapping("/thermometers/{thermometerId}")
    public List<Anomaly> getAnomaliesPerThermometer(@PathVariable("thermometerId") final String thermometerId) {
        return service.getAnomaliesPerThermometer(thermometerId);
    }

    @ResponseBody
    @GetMapping("/thermometers/threshold/{threshold}")
    public List<String> getThermometerOverAnomalyThreshold(@PathVariable("threshold") final Integer threshold) {
        return service.getThermometerOverAnomalyThreshold(threshold);
    }


}
