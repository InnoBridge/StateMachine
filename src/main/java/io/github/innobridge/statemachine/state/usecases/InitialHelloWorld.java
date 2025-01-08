package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;
import io.github.innobridge.statemachine.state.definition.State;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class InitialHelloWorld extends AbstractInitialState {
    
    @Override
    public void action() {
        System.out.println("Initializing Hello World");
    }

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new HelloWorld());
        transitions.put(new HelloWorld(), state -> new TerminalHelloWorld());
        this.transitions = transitions;
    }
}
