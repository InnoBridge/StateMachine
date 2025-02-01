package io.github.innobridge.statemachine.state.usecases.childinstances;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;

public class InitialMeal extends AbstractInitialState {

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatYouAte());
        transitions.put(new WhatYouAte(), state -> new ChildMeal());
        transitions.put(new ChildMeal(), state -> {
            ChildMeal childMeal = (ChildMeal) state;
            return new TerminalMeal(childMeal.getBreakfast(), childMeal.getLunch(), childMeal.getDinner());
        });
        this.transitions = transitions;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Starting day with empty stomach");
    }
    
}
