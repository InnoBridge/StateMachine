package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalHelloWorld extends AbstractTerminalState {
    
    @Override
    public void action() {
        System.out.println("Terminating Hello World");
    }

}
