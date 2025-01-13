package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalLunch extends AbstractTerminalState {

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Finished lunch");
    }
}
