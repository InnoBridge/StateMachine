package io.github.innobridge.statemachine.state.usecases.childinstances.dinner;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class Dinner extends AbstractBlockingTransitionState {

    private String dinner;
    
    public String getDinner() {
        return dinner;
    }
    
    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("For dinner we have: " + input.get().get("dinner").asText());
        setDinner(input.get().get("dinner").asText());
    }
    
}
