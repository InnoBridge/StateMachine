package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

public class WhatsForLunch extends AbstractNonBlockingTransitionState {

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("What's for breakfast?");
    }
    
}
