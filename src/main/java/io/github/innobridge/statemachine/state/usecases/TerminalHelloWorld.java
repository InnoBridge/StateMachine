package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

public class TerminalHelloWorld extends AbstractTerminalState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Terminating Hello World");
    }
}
