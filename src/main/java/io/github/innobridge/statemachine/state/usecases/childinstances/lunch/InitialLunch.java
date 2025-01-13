package io.github.innobridge.statemachine.state.usecases.childinstances.lunch;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;

public class InitialLunch extends AbstractInitialState {

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatsForLunch());
        transitions.put(new WhatsForLunch(), state -> new Lunch());
        transitions.put(new Lunch(), state -> new TerminalLunch());
        this.transitions = transitions;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Starting afternoon with empty stomach");
    }
    
}
