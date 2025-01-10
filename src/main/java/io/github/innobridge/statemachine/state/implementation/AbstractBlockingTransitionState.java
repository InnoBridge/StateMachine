package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.BlockingTransitionState;
import io.github.innobridge.statemachine.state.definition.State;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.function.Function;

@Document(collection = "States")
public abstract class AbstractBlockingTransitionState extends AbstractState implements BlockingTransitionState {

    @Override
    public void action() {
        // Implement blocking transition state action
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
    public State processing(Map<String, Function<State, State>> transitions) {
        action();
        State nextState = transition(transitions);
        nextState.setInstanceId(getInstanceId());
        return nextState;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
