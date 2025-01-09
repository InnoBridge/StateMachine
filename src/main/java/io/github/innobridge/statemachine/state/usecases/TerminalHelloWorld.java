package io.github.innobridge.statemachine.state.usecases;

import java.util.Map;
import java.util.function.Function;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalHelloWorld extends AbstractTerminalState {
    
    @Override
    public void action() {
        System.out.println("Terminating Hello World");
    }

}
