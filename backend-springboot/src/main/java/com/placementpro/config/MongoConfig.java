package com.placementpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // Auditing is enabled via @EnableMongoAuditing
    // MongoTemplate is auto-configured by Spring Boot
}

