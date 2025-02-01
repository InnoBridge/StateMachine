package io.github.innobridge.statemachine.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.llmtools.models.response.ToolCallFunction;
import io.github.innobridge.statemachine.publisher.RabbitMQProducer;
import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.repository.StateRepository;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.definition.BlockingTransitionState;
import io.github.innobridge.statemachine.state.definition.TerminalState;
import io.github.innobridge.statemachine.state.definition.ChildState;
import io.github.innobridge.statemachine.state.implementation.AbstractState;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
@Service
public class StateMachineService {

    @Autowired
    private ExecutionThreadRepository executionThreadRepository;
    @Autowired
    private StateRepository stateRepository;
    
    @Autowired
    private RabbitMQProducer rabbitMQProducer;

    public StateMachineService() {
    }
    
    public Map<String, Object> createStateMachine(InitialState initialState, Optional<JsonNode> input, Optional<String> parentId) {
        log.info("Creating state machine with initial state: {}", initialState.getClass().getSimpleName());
        ExecutionThread thread = initialState.createThread(parentId.orElse(null));
        executionThreadRepository.save(thread); 
        initialState.setInstanceId(thread.getId());
        State nextState = initialState.processing(initialState.getTransitions(), input);
        log.info("Transitioning to next state: {} for thread: {}", nextState.getClass().getSimpleName(), thread.getId());
        stateRepository.save((AbstractState) nextState);
        if (!nextState.isBlocking()) {
            log.debug("State is non-blocking, sending message to queue for thread: {}", thread.getId());
            rabbitMQProducer.sendMessage(thread.getId()); 
        } 
        return Map.of("threadId", thread.getId());
    }

    public Map<String, Object> createToolsStateMachine(InitialState initialState, List<ToolCallFunction> tools, Optional<JsonNode> input, Optional<String> parentId) {
        log.info("Creating state machine with initial state: {}", initialState.getClass().getSimpleName());
        ExecutionThread thread = initialState.createThread(parentId.orElse(null));
        thread.setTools(Optional.of(tools));
        executionThreadRepository.save(thread); 
        initialState.setInstanceId(thread.getId());
        State nextState = initialState.processing(initialState.getTransitions(), input);
        log.info("Transitioning to next state: {} for thread: {}", nextState.getClass().getSimpleName(), thread.getId());
        stateRepository.save((AbstractState) nextState);
        if (!nextState.isBlocking()) {
            log.debug("State is non-blocking, sending message to queue for thread: {}", thread.getId());
            rabbitMQProducer.sendMessage(thread.getId()); 
        } 
        return Map.of("threadId", thread.getId());
    }

    private InitialState createInitialState(String instanceType) {
        try {
            Class<?> stateClass = Class.forName(instanceType);
            if (!InitialState.class.isAssignableFrom(stateClass)) {
                throw new IllegalArgumentException("Class " + instanceType + " is not an InitialState");
            }
            try {
                // First try no-args constructor
                return (InitialState) stateClass.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                // If no-args constructor not found, try Map constructor
                return (InitialState) stateClass.getDeclaredConstructor(Map.class).newInstance(new HashMap<>());
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("State class not found: " + instanceType, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create initial state: " + instanceType, e);
        }
    }

    public Map<String, Object> processStateMachine(String instanceId) {
        return processStateMachine(instanceId, Optional.empty());
    }
    
    public Map<String, Object> processStateMachine(String instanceId, Optional<JsonNode> input) {
        return processStateMachine(instanceId, input, Optional.empty(), Optional.empty());
    }

    public Map<String, Object> processChildStateMachine(String parentId,
        String childId,
        Optional<Map<String, Object>> payload) {
        return processStateMachine(parentId, Optional.empty(), Optional.of(childId), payload);
    }

    public Map<String, Object> processStateMachine(String instanceId, 
        Optional<JsonNode> input, 
        Optional<String> childId,
        Optional<Map<String, Object>> payload
    ) {
        ExecutionThread thread = getExecutionThread(instanceId);
        InitialState initialState = createInitialState(thread.getInstanceType());
        State currentState = getState(instanceId, thread.getCurrentState());
        
        log.info("Current state: {}", currentState.getClass().getSimpleName());
        if (currentState instanceof TerminalState) {
            currentState.action(Optional.empty());
            return Map.of("threadId", processTerminalState(thread, 
            (TerminalState) currentState));
        }          
        
        AbstractState nextState;
        if (currentState instanceof ChildState childState) {
            nextState = (AbstractState) childState.processing(
                initialState.getTransitions(), 
                input,
                executionThreadRepository, 
                this,
                childId,
                payload);
        } else {
            nextState = (AbstractState) currentState.processing(initialState.getTransitions(), input);
        }        
        
        String result;
        switch (currentState) {
            case BlockingTransitionState blockingState -> {
                result = processBlockingTransitionState(nextState, thread);
            }
            case ChildState childState -> {
                result = processChildState(nextState, thread);
            }
            default -> throw new IllegalStateException("Unexpected state type: " + currentState.getClass().getSimpleName());
        }
        
        if (!nextState.isBlocking()) {
            log.debug("State is non-blocking, sending message to queue for thread: {}", thread.getId());
            rabbitMQProducer.sendMessage(thread.getId());
        }
        return Map.of("threadId", result);
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

    private String processChildState(
        AbstractState nextState,
        ExecutionThread thread
    ) {
        stateRepository.save(nextState);
        thread.setCurrentState(nextState.getClass().getName());
        executionThreadRepository.save(thread);
        return thread.getId();
    }

    private String processTerminalState(ExecutionThread thread, TerminalState terminalState) {
        stateRepository.deleteByInstanceId(thread.getId());
        executionThreadRepository.deleteById(thread.getId());
        if (thread.getParentId().isPresent()) {
            rabbitMQProducer.sendChildMessage(thread.getParentId().get(), thread.getId(), terminalState.getPayload().orElse(null));
        }
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

}
