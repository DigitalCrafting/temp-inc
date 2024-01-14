package io.kontak.apps.analytics.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "io.kontak.apps.analytics",
        "io.kontak.apps.anomaly.storage",
        "org.springdoc"
})
public class AnalyticsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsApiApplication.class, args);
    }
}
