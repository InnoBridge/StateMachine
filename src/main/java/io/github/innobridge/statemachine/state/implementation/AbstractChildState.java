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
    private boolean dispatched = false;
    private boolean blocking = false;

    private Set<String> childIds;

    public AbstractChildState() {
        super();
    }
    
    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    @Override
    public State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input, ExecutionThreadRepository executionThreadRepository, StateMachineService stateMachineService) {
        if (!isDispatched()) {
            setChildIds(dispatch(stateMachineService));
            setDispatched(true);
            if (!childIds.isEmpty()) {
                setBlocking(true);
            }
            return this;
        }
        if (completedChildInstances(childIds, executionThreadRepository)) {
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
                .map(childInstance -> stateMachineService.createStateMachine(childInstance, Optional.empty(), Optional.of(instanceId)))
                .collect(Collectors.toSet());
    }
    
    private boolean completedChildInstances(Set<String> childIds, ExecutionThreadRepository executionThreadRepository) {
        return !executionThreadRepository.existsByIdIn(childIds);
    }

    private void setChildIds(Set<String> childIds) {
        this.childIds = childIds;
    }
    
    private void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    @Override
    public boolean isBlocking() {
        return blocking;
    }
}
