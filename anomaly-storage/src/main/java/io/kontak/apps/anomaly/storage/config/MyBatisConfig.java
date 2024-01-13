package io.kontak.apps.anomaly.storage.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("io.kontak.apps.anomaly.storage")
public class MyBatisConfig {
}
