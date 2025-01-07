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
    public State transition(Map<State, Function<State, State>> transitions) {
        Function<State, State> transition = transitions.get(this);
        return transition != null ? transition.apply(this) : this;
    }

    @Override
    public State processing(Map<State, Function<State, State>> transitions) {
        action();
        var nextState = transition(transitions);
        // saveState(nextState);
        return nextState;
    }

}
