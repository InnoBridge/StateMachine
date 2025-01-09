package io.github.innobridge.statemachine.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    public String createStateMachine(InitialState initialState) {
        ExecutionThread thread = initialState.createThread();
        executionThreadRepository.save(thread); 
        initialState.setInstanceId(thread.getId());
        State nextState = initialState.processing(initialState.getTransitions());
        System.out.println("Next state: " + nextState.getClass().getSimpleName());
        stateRepository.save((AbstractState) nextState);
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
        ExecutionThread thread = getExecutionThread(instanceId);
        InitialState initialState = createInitialState(thread.getInstanceType());
        State currentState = getState(instanceId, thread.getCurrentState());
        System.out.println("Current state: " + currentState.getClass().getSimpleName());
        switch (currentState) {
            case BlockingTransitionState blockingState -> {
                return processBlockingTransitionState(blockingState, thread, initialState.getTransitions());
            }
            case TerminalState terminalState -> {
                return processTerminalState(thread);
            }
            default -> throw new IllegalStateException("Unexpected state type: " + currentState.getClass().getSimpleName());
        }
    }

    private String processBlockingTransitionState(
        BlockingTransitionState currentState, 
        ExecutionThread thread, 
        Map<String, Function<State, State>> transitions
    ) {
        AbstractState nextState = (AbstractState) currentState.processing(transitions);
        thread.setCurrentState(nextState.getClass().getName());
        executionThreadRepository.save(thread);
        stateRepository.save(nextState);
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
