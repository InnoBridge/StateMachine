package io.github.innobridge.statemachine.state.definition;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.service.StateMachineService;

public interface ChildState extends State {
    State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input, 
            ExecutionThreadRepository executionThreadRepository, StateMachineService stateMachineService);
            
    default State processing(Map<String, Function<State, State>> transitions, ExecutionThreadRepository executionThreadRepository, StateMachineService stateMachineService) {
        return processing(transitions, null, executionThreadRepository, stateMachineService);
    }
    
    @Override
    default State processing(Map<String, Function<State, State>> states, Optional<JsonNode> input) {
        throw new UnsupportedOperationException("ChildState must use processing with ExecutionThreadRepository");
    }

    List<InitialState> registerChildInstances();
}
