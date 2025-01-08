package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class TerminalHelloWorld extends AbstractBlockingTransitionState {
    
    @Override
    public void action() {
        System.out.println("Terminating Hello World");
    } 
}
