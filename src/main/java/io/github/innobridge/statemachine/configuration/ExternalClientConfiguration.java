package io.github.innobridge.statemachine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ExternalClientConfiguration {

    @Bean
    public MongoClient mongoClient(@Value("${MONGO_DATABASE_URI:}") String mongoDatabaseUri) {
        if (mongoDatabaseUri == null || mongoDatabaseUri.isEmpty()) {
            String errorMessage = "MONGO_DATABASE_URI not set in the environment variables";
            log.warn(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoDatabaseUri))
                .applyToSslSettings(builder -> builder.enabled(false))
                .build();

        return MongoClients.create(settings);
    }
}
