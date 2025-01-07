package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.TerminalState;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "States")
public abstract class AbstractTerminalState extends AbstractState implements TerminalState {
 
    @Override
    public void action() {
        // Implement terminal state action
    }

}
