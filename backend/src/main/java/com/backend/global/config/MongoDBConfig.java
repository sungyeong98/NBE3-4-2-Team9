package com.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = {"com.backend.domain.chat.repository"})
@EnableMongoAuditing
public class MongoDBConfig {

	@Value("${spring.data.mongodb.uri}")
	private String uri;

	@Bean
	public MongoDatabaseFactory mongoDatabaseFactory() {
		return new SimpleMongoClientDatabaseFactory(uri);
	}


	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDatabaseFactory());
	}
}
