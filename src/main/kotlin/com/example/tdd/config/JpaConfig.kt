package com.example.tdd.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan

@Configuration
@EnableJpaRepositories(basePackages = ["com.example.tdd.domain.repository"])
@EntityScan(basePackages = ["com.example.tdd.domain.entity"])
class JpaConfig
