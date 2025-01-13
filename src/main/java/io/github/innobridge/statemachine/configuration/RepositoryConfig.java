package io.github.innobridge.statemachine.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;

@Configuration
public class RepositoryConfig {
    public static final String STATE_MACHINE = "StateMachine";

    @Autowired
    private MongoClient mongoClient;

    @Value("${statemachine.history.enabled:true}")
    private boolean historyEnabled;

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient, STATE_MACHINE);
    }

    public boolean isHistoryEnabled() {
        return historyEnabled;
    }
}
