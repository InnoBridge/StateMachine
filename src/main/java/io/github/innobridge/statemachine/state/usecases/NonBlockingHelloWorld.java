package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

public class NonBlockingHelloWorld extends AbstractNonBlockingTransitionState {
    
    private String name;

    public NonBlockingHelloWorld(String name) {
        super();
        this.name = name;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Non BlockingHello World " + name);
    } 
}
