package io.github.innobridge.statemachine.state.usecases.childinstances.dinner;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalDinner extends AbstractTerminalState {

    private String dinner;

    public TerminalDinner(String dinner) {
        super();
        this.dinner = dinner;
    }

    public String getDinner() {
        return dinner;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Finished dinner");
    }

    @Override
    public Optional<Map<String, Object>> getPayload() {
        return Optional.of(Map.of("dinner", getDinner()));
    }
}
