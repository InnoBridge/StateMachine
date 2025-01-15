package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.State;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.STATES;

@Document(collection = STATES)
public abstract class AbstractState implements State {
    @Id
    private String id;
    public String instanceId; 

    @Override
    public State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input) {
        action(input);
        State nextState = transition(transitions);
        nextState.setInstanceId(getInstanceId());
        return nextState;    
    }

    @Override
    public State transition(Map<String, Function<State, State>> transitions) {
        Function<State, State> transition = transitions.get(this.getClass().getName());
        if (transition == null) {
            throw new IllegalStateException("No transition found for " + this.getClass().getSimpleName());
        }
        return transition.apply(this);
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
