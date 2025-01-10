package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.definition.TerminalState;

import java.util.Map;
import java.util.function.Function;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "States")
public abstract class AbstractTerminalState extends AbstractState implements TerminalState {
 
    @Override
    public void action() {
        // Implement terminal state action
    }

    @Override
    public State transition(Map<String, Function<State, State>> transitions) {
        return this;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

}
