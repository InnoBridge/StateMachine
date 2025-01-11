package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractInitialState;
import io.github.innobridge.statemachine.state.definition.State;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

public class InitialHelloWorld extends AbstractInitialState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Initializing Hello World");
    }

    @Override
    public void setTransitions() {
        Map<State, Function<State, State>> transitions = new HashMap<>();
        transitions.put(this, state -> new WhatIsYourName());
        transitions.put(new WhatIsYourName(), state -> new HelloWorld());
        transitions.put(new HelloWorld(), state -> {
            HelloWorld helloWorld = (HelloWorld) state;
            return new NonBlockingHelloWorld(helloWorld.getName());
        });
        transitions.put(new NonBlockingHelloWorld(null), state -> new TerminalHelloWorld());
        this.transitions = transitions;
    }
}
