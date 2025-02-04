package io.github.innobridge.statemachine.state.usecases.childinstances.dinner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;

public class InitialDinner extends AbstractInitialState {

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatsForDinner());
        transitions.put(new WhatsForDinner(), state -> new Dinner());
        transitions.put(new Dinner(), state -> {
            Dinner dinner = (Dinner) state;
            return new TerminalDinner(dinner.getDinner());
        });
        this.transitions = transitions;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Starting evening with empty stomach");
    }
    
}
