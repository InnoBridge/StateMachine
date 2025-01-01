package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class ExecutionThread {

    private UUID workflowId;
    private Optional<UUID> parentId;
    private Set<UUID> stateIds;

    private Map<State, Function<State, State>> states;
    
    public UUID getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(UUID workflowId) {
        this.workflowId = workflowId;
    }

    public Set<UUID> getStateIds() {
        return stateIds;
    }

    public void setStateIds(Set<UUID> stateIds) {
        this.stateIds = stateIds;
    }

    public Map<State, Function<State, State>> getStates() {
        return states;
    }

    public void setStates(Map<State, Function<State, State>> states) {
        this.states = states;
    }
}
