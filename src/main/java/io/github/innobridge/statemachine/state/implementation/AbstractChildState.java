package io.github.innobridge.statemachine.state.implementation;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.service.StateMachineService;
import io.github.innobridge.statemachine.state.definition.ChildState;
import io.github.innobridge.statemachine.state.definition.State;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.STATES;

@Document(collection = STATES)
public abstract class AbstractChildState extends AbstractState implements ChildState {
    boolean dispatched;

    Set<String> childIds;

    public AbstractChildState() {
        super();
        setBlocking(false);
        setDispatched(false);
    }
    
    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    @Override
    public State processing(Map<String, Function<State, State>> transitions, 
    Optional<JsonNode> input, 
    ExecutionThreadRepository executionThreadRepository, 
    StateMachineService stateMachineService,
    Optional<String> childId) {
        if (!isDispatched()) {
            setChildIds(dispatch(stateMachineService));
            setDispatched(true);
            if (!childIds.isEmpty()) {
                setBlocking(true);
            }
            return this;
        }
        if (childId.isPresent()) {
            Set<String> childIds = getChildIds();
            childIds.remove(childId.get());
            setChildIds(childIds);
        }
        if (completedChildInstances(childIds, executionThreadRepository) && childIds.isEmpty()) {
            setDispatched(false);
            setBlocking(false);
            State nextState = transition(transitions);
            nextState.setInstanceId(getInstanceId());
            return nextState;
        }
        return this;
    }

    private Set<String> dispatch(StateMachineService stateMachineService) {
        return registerChildInstances().stream()
                .map(childInstance -> stateMachineService.createStateMachine(childInstance, Optional.empty(), Optional.of(instanceId)).get("threadId").toString())
                .collect(Collectors.toSet());
    }
    
    private boolean completedChildInstances(Set<String> childIds, ExecutionThreadRepository executionThreadRepository) {
        return !executionThreadRepository.existsByIdIn(childIds);
    }

    Set<String> getChildIds() {
        return childIds;
    }    

    void setChildIds(Set<String> childIds) {
        this.childIds = childIds;
    }
    
    @Override
    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public boolean isBlocking() {
        return blocking;
    }
}
