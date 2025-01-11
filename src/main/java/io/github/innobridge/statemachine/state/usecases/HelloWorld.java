package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

public class HelloWorld extends AbstractBlockingTransitionState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        input.ifPresentOrElse(json -> {
            System.out.println("Hello " + json.get("name").asText());
        }, () -> {
            System.out.println("Hello World without input");
        });
    } 
}
