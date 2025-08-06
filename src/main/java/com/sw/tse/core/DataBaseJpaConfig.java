package com.sw.tse.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ConditionalOnProperty(name = "database.enabled", havingValue = "true")
@EnableJpaRepositories(basePackages = "com.sw.tse.domain.repository")
@EntityScan(basePackages = "com.sw.tse.domain.model")
public class DataBaseJpaConfig  {

}
