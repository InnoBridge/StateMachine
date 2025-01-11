package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.NonBlockingTransitionState;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "States")
public abstract class AbstractNonBlockingTransitionState extends AbstractBlockingTransitionState implements NonBlockingTransitionState {

    @Override
    public boolean isBlocking() {
        return false;
    }
     
}
