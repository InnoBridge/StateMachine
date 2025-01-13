package io.github.innobridge.statemachine.state.usecases.childinstances.breakfast;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

public class WhatsForBreakfast extends AbstractNonBlockingTransitionState {

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("What's for breakfast?");
    }
    
}
