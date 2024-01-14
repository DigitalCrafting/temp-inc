package io.kontakt.apps.temperature.generator;

import io.kontakt.apps.event.TemperatureReading;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
* What and why:
*
* I define a map of rooms and thermometers, because if it was random every time,
* we couldn't test analytics api at all.
* Besides, it doesn't make sense to have random values each time.
*
* I also assumed, that a thermometer is bound to a room, each room will then have expected
* average temperature, it doesn't make sense to measure average between let's say, kitchen and garage.
*
* For each measurement, we select random room and random thermometer within a room.
*
* Normally, the temperature should not fluctuate between 10 and 30 degrees,
* so I changed the random bound to be 19 to 22, which is more plausible.
*
* In order to generate anomaly from time to time, I will generate another random
* and check if it's divisible by 11. We don't want anomaly too often,
* or it will appear as something withing threshold. On the other hand, having anomaly sparsely
* is not that bad, although it might not be generated often enough to test extensively.
* In summary: it's hard to test random events, this is good enough for a weekend task.
*
* Let's also make sure, that there is at least 1 thermometer without any anomalies, mainly for analytics testing,
* and let's use 'therm_4' for that. This thermometer should not pop up in any queries to anomalies db.
* */
@Log
@Component
public class SimpleTemperatureGenerator implements TemperatureGenerator {

    public static final String SAFE_THERM_ID = "therm_4";
    private final Random random = new Random();
    private final Map<String, List<String>> roomsMap = new HashMap<>();

    public SimpleTemperatureGenerator() {
        roomsMap.put("room_0", List.of("therm_0", "therm_1", "therm_2"));
        roomsMap.put("room_1", List.of("therm_3", "therm_4", "therm_5"));
        roomsMap.put("room_2", List.of("therm_6", "therm_7", "therm_8"));
    }

    @Override
    public List<TemperatureReading> generate() {
        return List.of(generateSingleReading());
    }

    private TemperatureReading generateSingleReading() {
        int randomRoom = random.nextInt(0, 20) % roomsMap.keySet().size();
        String roomId = "room_" + randomRoom;
        List<String> thermometers = roomsMap.get(roomId);
        int randomTherm = random.nextInt(0, 20) % thermometers.size();
        String thermometerId = thermometers.get(randomTherm);

        double tempReading = random.nextDouble(18d, 22d);

        if (!SAFE_THERM_ID.equals(thermometerId) && random.nextInt(0, 100) % 11 == 0) {
            double anomaly = random.nextDouble(6, 10);
            tempReading += anomaly;
        }

        TemperatureReading reading = new TemperatureReading(
                tempReading,
                roomId,
                thermometerId,
                Instant.now()
        );
        log.info("Generated reading: " + reading);
        return reading;
    }
}
