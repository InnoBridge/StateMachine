package io.github.innobridge.statemachine.state.definition;

public interface NonBlockingTransitionState extends BlockingTransitionState {
    void enqueue();
}
