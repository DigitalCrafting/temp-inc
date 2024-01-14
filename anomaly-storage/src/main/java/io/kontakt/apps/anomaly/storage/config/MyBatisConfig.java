package io.kontakt.apps.anomaly.storage.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.kontakt.apps.anomaly.storage")
public class MyBatisConfig {
}
