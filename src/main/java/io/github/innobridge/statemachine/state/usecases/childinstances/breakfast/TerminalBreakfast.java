package io.github.innobridge.statemachine.state.usecases.childinstances.breakfast;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalBreakfast extends AbstractTerminalState {

    private String breakfast;

    public TerminalBreakfast(String breakfast) {
        super();
        this.breakfast = breakfast;
    }

    public String getBreakfast() {
        return breakfast;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Finished breakfast");
    }

    @Override
    public Optional<Map<String, Object>> getPayload() {
        return Optional.of(Map.of("breakfast", breakfast));
    }

}
