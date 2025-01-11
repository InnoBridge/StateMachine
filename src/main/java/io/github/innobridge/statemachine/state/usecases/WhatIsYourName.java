package io.github.innobridge.statemachine.state.usecases;

import io.github.innobridge.statemachine.state.implementation.AbstractNonBlockingTransitionState;

import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

public class WhatIsYourName extends AbstractNonBlockingTransitionState {
    
    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("What is your name?");
    } 
}
