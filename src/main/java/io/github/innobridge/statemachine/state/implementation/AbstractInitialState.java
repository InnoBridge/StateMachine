package io.github.innobridge.statemachine.state.implementation;

import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Document(collection = "States")
public abstract class AbstractInitialState extends AbstractState implements InitialState {

    public Map<State, Function<State, State>> transitions;

    public AbstractInitialState() {
        super();
        setTransitions();
    }

    @Override
    public void action() {
        // Implement initial state action
    }

    @Override
    public UUID generateId() {
        return UUID.randomUUID();
    }

    @Override
    public State transition(Map<String, Function<State, State>> transitions) {
        Function<State, State> transition = transitions != null ? transitions.get(this) : null;
        return transition != null ? transition.apply(this) : this;
    }

    @Override
    public ExecutionThread createThread(String parentId) {
        ExecutionThread thread = new ExecutionThread();
        thread.setId(generateId().toString());
        thread.setInstanceType(this.getClass().getName());
        if (parentId != null) {
            thread.setParentId(Optional.of(parentId));
        }
        thread.setChildIds(new HashSet<>());
        State nextState = processing(getTransitions());
        thread.setCurrentState(nextState.getClass().getName());
        return thread;
    }

    @Override
    public Map<String, Function<State, State>> getTransitions() {
        Map<String, Function<State, State>> result = new HashMap<>();
        transitions.forEach(
            (key, value) -> {
                result.put(key.getClass().getName(), value);
            }
        );
        return result;
    }

    @Override
    public State processing(Map<String, Function<State, State>> states) {
        action();
        State nextState = transition(states);
        nextState.setInstanceId(getInstanceId());
        return nextState;
    } 
}
