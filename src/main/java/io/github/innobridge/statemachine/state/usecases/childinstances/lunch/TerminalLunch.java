package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalLunch extends AbstractTerminalState {

    private String lunch;

    public TerminalLunch(String lunch) {
        super();
        this.lunch = lunch;
    }

    public String getLunch() {
        return lunch;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Finished lunch");
    }

    @Override
    public Optional<Map<String, Object>> getPayload() {
        return Optional.of(Map.of("lunch", lunch));
    }
}
