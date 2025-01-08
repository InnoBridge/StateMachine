package io.github.innobridge.statemachine.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mongodb.client.MongoClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MongoHealthCheck implements CommandLineRunner {

    private final MongoClient mongoClient;

    @Autowired
    public MongoHealthCheck(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            mongoClient.listDatabaseNames().first();
            log.info("Successfully connected to MongoDB.");
        } catch (Exception e) {
            log.error("Failed to connect to MongoDB. Check if the IP is whitelisted.", e);
            // Use System.exit(1) to shut down the application on connection failure
            System.exit(1);
        }
    }
}