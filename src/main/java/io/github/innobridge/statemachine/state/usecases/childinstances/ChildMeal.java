package io.github.innobridge.statemachine.state.usecases.childinstances;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.implementation.AbstractChildState;
import io.github.innobridge.statemachine.state.usecases.childinstances.breakfast.InitialBreakfast;
import io.github.innobridge.statemachine.state.usecases.childinstances.dinner.InitialDinner;
import io.github.innobridge.statemachine.state.usecases.childinstances.lunch.InitialLunch;

public class ChildMeal extends AbstractChildState {

    private String breakfast;
    private String lunch;
    private String dinner;

    String getBreakfast() { return breakfast; } 
    String getLunch() { return lunch; } 
    String getDinner() { return dinner; } 

    void setBreakfast(String breakfast) { this.breakfast = breakfast; } 
    void setLunch(String lunch) { this.lunch = lunch; } 
    void setDinner(String dinner) { this.dinner = dinner; }

    @Override
    public List<InitialState> registerChildInstances() {
        return List.of(
                new InitialBreakfast(),
                new InitialLunch(),
                new InitialDinner()
        );
    }

    @Override
    public void action(Map<String, Object> input) {
        if (input.containsKey("breakfast")) {
            setBreakfast(input.get("breakfast").toString());
        }
        if (input.containsKey("lunch")) {
            setLunch(input.get("lunch").toString());
        }
        if (input.containsKey("dinner")) {
            setDinner(input.get("dinner").toString());
        }
    }
}
