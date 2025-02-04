package io.github.innobridge.statemachine.state.usecases.childinstances;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.state.implementation.AbstractTerminalState;

public class TerminalMeal extends AbstractTerminalState {

    private final String breakfast;
    private final String lunch;
    private final String dinner;

    public TerminalMeal(String breakfast, String lunch, String dinner) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.dinner = dinner;
    }

    @Override
    public void action(Optional<JsonNode> input) {
        System.out.println("Final Meal:");
        System.out.println("Breakfast: " + breakfast);
        System.out.println("Lunch: " + lunch);
        System.out.println("Dinner: " + dinner);
    }

    @Override
    public Optional<Map<String, Object>> getPayload() {
        return Optional.of(Map.of("breakfast", breakfast, "lunch", lunch, "dinner", dinner));
    }
    
}
