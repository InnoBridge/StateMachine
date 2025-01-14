package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.BlockingTransitionState;
import io.github.innobridge.statemachine.state.definition.State;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.STATES;

@Document(collection = STATES)
public abstract class AbstractBlockingTransitionState extends AbstractState implements BlockingTransitionState {


    @Override
    public State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input) {
        action(input);
        State nextState = transition(transitions);
        nextState.setInstanceId(getInstanceId());
        return nextState;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
