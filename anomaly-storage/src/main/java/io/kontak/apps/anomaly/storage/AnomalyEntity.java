package io.kontak.apps.anomaly.storage;

import lombok.Data;

import java.time.Instant;

@Data
public class AnomalyEntity {
    private int id;
    private double temperature;
    private String roomId;
    private String thermometerId;
    private Instant timestamp;
}
