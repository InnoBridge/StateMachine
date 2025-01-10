package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

public class NonBlockingHelloWorld extends AbstractNonBlockingTransitionState {
    
    @Override
    public void action() {
        System.out.println("Non BlockingHello World");
    } 
}
