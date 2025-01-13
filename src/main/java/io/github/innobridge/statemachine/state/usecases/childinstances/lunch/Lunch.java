package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class Lunch extends AbstractBlockingTransitionState {

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("For lunch we have: " + input.get().get("lunch").asText());
    }
    
}
