package io.github.innobridge.statemachine.state.implementation;

import java.util.List;
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
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;

@Document(collection = "States")
public abstract class AbstractChildState extends AbstractState implements ChildState {
    private boolean dispatched = false;
    private boolean blocking = false;

    private List<InitialState> childInstances;
    private Set<String> childIds;

    public AbstractChildState() {
        super();
        this.childInstances = registerChildInstances();
    }
    
    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }

    @Override
    public List<InitialState> getChildInstances() {
        return childInstances;
    }

    @Override
    public void setChildInstances(List<InitialState> childInstances) {
        this.childInstances = childInstances;
    }

    @Override
    public State processing(Map<String, Function<State, State>> transitions, Optional<JsonNode> input, ExecutionThreadRepository executionThreadRepository, StateMachineService stateMachineService) {
        if (!isDispatched()) {
            setChildIds(dispatch(stateMachineService));
            setDispatched(true);
            setBlocking(true);
            return this;
        }
        if (completedChildInstances(childIds, executionThreadRepository)) {
            setDispatched(false);
            setBlocking(false);
            return transition(transitions);
        }
        return this;
    }

    private Set<String> dispatch(StateMachineService stateMachineService) {
        return childInstances.stream()
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
