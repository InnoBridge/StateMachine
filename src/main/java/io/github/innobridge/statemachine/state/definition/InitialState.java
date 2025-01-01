package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface InitialState extends State {
    UUID generateId();
    State transition(Map<State, Function<State, State>> transitions);
    ExecutionThread createThread();
}
