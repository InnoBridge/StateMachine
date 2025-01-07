package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.State;

import java.util.Map;
import java.util.function.Function;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "States")
public abstract class AbstractState implements State {
    @Id
    private String id;
    private String instanceId; 
    
    @Override
    public abstract void action();

    @Override
    public abstract State processing(Map<State, Function<State, State>> transitions);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

}
