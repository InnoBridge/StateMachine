package io.github.innobridge.statemachine.state.usecases.childinstances;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

public class WhatYouAte extends AbstractNonBlockingTransitionState {

    @Override
    public void action(Optional<JsonNode> input) {
       System.out.println("What did you eat?");
    }
    
}
