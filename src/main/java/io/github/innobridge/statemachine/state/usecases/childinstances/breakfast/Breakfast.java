package io.github.innobridge.statemachine.state.usecases.childinstances.breakfast;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class Breakfast extends AbstractBlockingTransitionState {

    private String breakfast;

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("For breakfast we have: " + input.get().get("breakfast").asText());
        setBreakfast(input.get().get("breakfast").asText());
    }
    
}
