package io.kontak.apps.anomaly.detector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "io.kontak.apps.anomaly.storage",  "io.kontak.apps.anomaly.detector"})
@SpringBootApplication
public class AnomalyDetectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnomalyDetectorApplication.class, args);
    }
}
