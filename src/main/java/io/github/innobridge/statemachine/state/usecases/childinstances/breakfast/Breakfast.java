package io.github.innobridge.statemachine.state.usecases.childinstances.breakfast;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class Breakfast extends AbstractBlockingTransitionState {

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("For breakfast we have: " + input.get().get("breakfast").asText());
    }
    
}
