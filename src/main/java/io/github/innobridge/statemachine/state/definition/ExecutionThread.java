package io.github.innobridge.statemachine.state.definition;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "StateMachineInstance")
@Data
public class ExecutionThread {

    @Id
    private String id;

    private String instanceType;
    private String parentId;
    private Set<String> childIds;
    private String currentState;

    public Optional<String> getParentId() {
        return Optional.ofNullable(parentId);
    }

    public void setParentId(Optional<String> parentId) {
        this.parentId = parentId.orElse(null);
    }

    public Set<String> childIds() {
        return childIds;
    }

    public void setChildIds(Set<String> childIds) {
        this.childIds = childIds;
    }
}
