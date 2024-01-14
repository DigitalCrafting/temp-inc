package io.kontakt.apps.anomaly.detector;

import io.kontakt.apps.anomaly.detector.storage.AnomaliesDatabaseService;
import io.kontakt.apps.event.Anomaly;
import io.kontakt.apps.event.TemperatureReading;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class TemperatureMeasurementsListenerTest extends AbstractIntegrationTest {

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-in-0.destination}")
    private String inputTopic;

    @Value("${spring.cloud.stream.bindings.anomalyDetectorProcessor-out-0.destination}")
    private String outputTopic;

    @MockBean
    private AnomaliesDatabaseService databaseService;

    @Test
    void testInOutFlow() {
        doNothing().when(databaseService).saveAnomaly(any());

        try (TestKafkaConsumer<Anomaly> consumer = new TestKafkaConsumer<>(
                kafkaContainer.getBootstrapServers(),
                outputTopic,
                Anomaly.class
        );
             TestKafkaProducer<TemperatureReading> producer = new TestKafkaProducer<>(
                     kafkaContainer.getBootstrapServers(),
                     inputTopic
             )) {

            TemperatureReading temperatureReading;
            Instant time = Instant.now();
            for (int i = 0; i < 10; i++) {
                time = time.plusSeconds(1);
                if (i == 5) {
                    temperatureReading = new TemperatureReading(27d, "room", "thermometer5", time);
                } else {
                    temperatureReading = new TemperatureReading(20d, "room", "thermometer" + i, time);
                }
                producer.produce(temperatureReading.thermometerId(), temperatureReading);
            }
            consumer.drain(
                    consumerRecords -> consumerRecords.stream().anyMatch(r -> r.value().thermometerId().equals("thermometer5")),
                    Duration.ofSeconds(5)
            );
        }
    }
}
