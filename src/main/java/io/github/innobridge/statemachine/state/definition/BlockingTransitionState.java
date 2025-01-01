package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.function.Function;

public interface BlockingTransitionState extends TransitionState {
    void enqueue();
}
