package io.github.innobridge.statemachine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.repository.StateRepository;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;
import io.github.innobridge.statemachine.state.implementation.AbstractState;

@Service
public class StateMachineService {

    @Autowired
    private ExecutionThreadRepository executionThreadRepository;

    @Autowired
    private StateRepository stateRepository;
    
    public String createStateMachine(InitialState initialState) {
        ExecutionThread thread = initialState.createThread();
        System.out.println("Thread id: " + thread.getId());
        executionThreadRepository.save(thread); 
        initialState.setInstanceId(thread.getId());
        System.out.println("initial state instance id: " + initialState.getInstanceId());
        State nextState = initialState.processing(initialState.getTransitions());
        System.out.println("next state instance id: " + nextState.getInstanceId());
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
        State currentState = getState(instanceId, initialState);
        System.out.println("Current state: " + currentState.getClass().getSimpleName());
        AbstractState nextState = (AbstractState) currentState.processing(initialState.getTransitions());
        thread.setCurrentState(nextState.getClass().getName());
        executionThreadRepository.save(thread);
        stateRepository.save(nextState);
        return thread.getId();
    }

    public ExecutionThread getExecutionThread(String instanceId) {
        return executionThreadRepository.findById(instanceId).get();
    }

    public State getState(String instanceId, State state) {
         List<AbstractState> states = stateRepository.findByInstanceId(instanceId);
         if (states.isEmpty()) {
             throw new IllegalStateException("No state found for instanceId " + instanceId);
         }
         return states.get(0);
    }

    // JsonNode getState(String instanceId, State state) {
    //     List<AbstractState> states = stateRepository.findByInstanceIdAndClass(instanceId, state.getClass().getSimpleName());
    //     return states.get(0).toJson();
    // }
}
