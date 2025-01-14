package io.github.innobridge.statemachine.state.definition;

import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.STATE_MACHINE_INSTANCE;

@Document(collection = STATE_MACHINE_INSTANCE)
@Data
public class ExecutionThread {

    @Id
    private String id;

    private String instanceType;
    private String parentId;
    private String currentState;

    public Optional<String> getParentId() {
        return Optional.ofNullable(parentId);
    }

    public void setParentId(Optional<String> parentId) {
        this.parentId = parentId.orElse(null);
    }

}
