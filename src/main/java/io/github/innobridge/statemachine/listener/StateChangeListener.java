package io.github.innobridge.statemachine.listener;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.HISTORY;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractState;
import io.github.innobridge.statemachine.configuration.RepositoryConfig;

@Component
public class StateChangeListener extends AbstractMongoEventListener<AbstractState> {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Override
    public void onAfterSave(AfterSaveEvent<AbstractState> event) {
        if (!repositoryConfig.isHistoryEnabled()) {
            return;
        }

        String collectionName = ((MongoMappingEvent<?>) event).getCollectionName();
        if (HISTORY.equals(collectionName)) {
            return; // Skip if this is already a history state
        }
        
        AbstractState state = event.getSource();
        
        // Create a copy for history without the _id field
        AbstractState historyCopy = new AbstractState() {
            @Override
            public State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input) {
                return null; // Not needed for history
            }

            @Override
            public void action(Optional<JsonNode> input) {
                // Not needed for history
            }

            @Override
            public boolean isBlocking() {
                return state.isBlocking();
            }
        };
        historyCopy.setInstanceId(state.getInstanceId());
        mongoTemplate.insert(historyCopy, HISTORY);
    }
}
