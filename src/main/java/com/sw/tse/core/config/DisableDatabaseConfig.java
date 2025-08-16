package com.sw.tse.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "database.enabled", havingValue = "false", matchIfMissing = true)
@Import(DisableJpaConfiguration.class)
public class DisableDatabaseConfig {
}
