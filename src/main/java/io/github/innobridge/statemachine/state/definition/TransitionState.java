package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.function.Function;

public interface TransitionState extends State {
    State transition(Map<State, Function<State, State>> states);
}
