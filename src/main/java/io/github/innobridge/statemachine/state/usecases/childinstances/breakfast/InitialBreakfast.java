package io.github.innobridge.statemachine.state.usecases.childinstances.breakfast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;

public class InitialBreakfast extends AbstractInitialState {

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatsForBreakfast());
        transitions.put(new WhatsForBreakfast(), state -> new Breakfast());
        transitions.put(new Breakfast(), state -> {
            Breakfast breakfast = (Breakfast) state;
            return new TerminalBreakfast(breakfast.getBreakfast());
        });
        this.transitions = transitions;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Starting morning with empty stomach");
    }
    
}
