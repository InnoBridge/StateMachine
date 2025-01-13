package io.github.innobridge.statemachine.state.usecases.childinstances;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.implementation.AbstractChildState;
import io.github.innobridge.statemachine.state.usecases.childinstances.breakfast.InitialBreakfast;
import io.github.innobridge.statemachine.state.usecases.childinstances.dinner.InitialDinner;
import io.github.innobridge.statemachine.state.usecases.childinstances.lunch.InitialLunch;

public class ChildMeal extends AbstractChildState {

    @Override
    public List<InitialState> registerChildInstances() {
        return List.of(
                new InitialBreakfast(),
                new InitialLunch(),
                new InitialDinner()
        );
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Having breakfast, lunch and dinner");
    }
    
}
