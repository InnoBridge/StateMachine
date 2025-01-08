package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface InitialState extends State {
    UUID generateId();
    State transition(Map<String, Function<State, State>> transitions);
    ExecutionThread createThread(String parentId);
    default ExecutionThread createThread() {
        return createThread(null);
    }
    Map<String, Function<State, State>> getTransitions();
    void setTransitions();
}
