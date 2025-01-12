package io.github.innobridge.statemachine.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.publisher.RabbitMQProducer;
import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.repository.StateRepository;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.definition.BlockingTransitionState;
import io.github.innobridge.statemachine.state.definition.TerminalState;
import io.github.innobridge.statemachine.state.implementation.AbstractState;

@Service
public class StateMachineService {

    @Autowired
    private ExecutionThreadRepository executionThreadRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private RabbitMQProducer rabbitMQProducer;
    
    public String createStateMachine(InitialState initialState, Optional<JsonNode> input, Optional<String> parentId) {
        System.out.println("Initial state: " + initialState.getClass().getSimpleName());
        ExecutionThread thread = initialState.createThread(parentId.orElse(null));
        executionThreadRepository.save(thread); 
        initialState.setInstanceId(thread.getId());
        State nextState = initialState.processing(initialState.getTransitions(), input);
        System.out.println("Next state: " + nextState.getClass().getSimpleName());
        stateRepository.save((AbstractState) nextState);
        if (!nextState.isBlocking()) {
            rabbitMQProducer.sendMessage(thread.getId()); 
        } 
        return thread.getId();
    }

    private InitialState createInitialState(String instanceType) {
        try {
            Class<?> stateClass = Class.forName(instanceType);
            if (!InitialState.class.isAssignableFrom(stateClass)) {
                throw new IllegalArgumentException("Class " + instanceType + " is not an InitialState");
            }
            return (InitialState) stateClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("State class not found: " + instanceType, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create initial state: " + instanceType, e);
        }
    }

    public String processStateMachine(String instanceId) {
        return processStateMachine(instanceId, Optional.empty());
    }

    public String processStateMachine(String instanceId, Optional<JsonNode> input) {
        ExecutionThread thread = getExecutionThread(instanceId);
        InitialState initialState = createInitialState(thread.getInstanceType());
        State currentState = getState(instanceId, thread.getCurrentState());
        System.out.println("Current state: " + currentState.getClass().getSimpleName());
        if (currentState instanceof TerminalState) {
            return processTerminalState(thread);
        }  
        AbstractState nextState = (AbstractState) currentState.processing(initialState.getTransitions(), input);
        String result;
        switch (currentState) {
            case BlockingTransitionState blockingState -> {
                result = processBlockingTransitionState(nextState, thread);
            }
            default -> throw new IllegalStateException("Unexpected state type: " + currentState.getClass().getSimpleName());
        }
        if (!nextState.isBlocking()) {
            rabbitMQProducer.sendMessage(thread.getId()); 
        }
        return result;
    }

    private String processBlockingTransitionState(
        AbstractState nextState,
        ExecutionThread thread 
    ) {
        stateRepository.save(nextState);
        thread.setCurrentState(nextState.getClass().getName());
        executionThreadRepository.save(thread);
        return thread.getId();
    }

    private String processTerminalState(ExecutionThread thread) {
        stateRepository.deleteByInstanceId(thread.getId());
        executionThreadRepository.deleteById(thread.getId());
        return thread.getId();
    }

    private ExecutionThread getExecutionThread(String instanceId) {
        return executionThreadRepository.findById(instanceId).get();
    }

    private State getState(String instanceId, String stateClassName) {
        List<AbstractState> states = stateRepository.findByInstanceIdAndClass(instanceId, stateClassName);
        if (states.isEmpty()) {
            throw new IllegalStateException("No state found for instanceId " + instanceId);
        }
        return states.get(0);
    }

    private State getState(String instanceId, State state) {
        return getState(instanceId, state.getClass().getSimpleName());
    }

    // JsonNode getState(String instanceId, State state) {
    //     List<AbstractState> states = stateRepository.findByInstanceIdAndClass(instanceId, state.getClass().getSimpleName());
    //     return states.get(0).toJson();
    // }
}
