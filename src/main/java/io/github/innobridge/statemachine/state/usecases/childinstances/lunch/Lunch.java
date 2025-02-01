package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractBlockingTransitionState;

public class Lunch extends AbstractBlockingTransitionState {

    private String lunch;

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("For lunch we have: " + input.get().get("lunch").asText());
        setLunch(input.get().get("lunch").asText());
    }
    
}
