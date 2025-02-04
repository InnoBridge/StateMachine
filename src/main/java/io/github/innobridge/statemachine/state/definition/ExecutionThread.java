package io.github.innobridge.statemachine.state.definition;

import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import io.github.innobridge.llmtools.models.response.ToolCallFunction;
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
    private List<ToolCallFunction> tools;

    public Optional<String> getParentId() {
        return Optional.ofNullable(parentId);
    }

    public void setParentId(Optional<String> parentId) {
        this.parentId = parentId.orElse(null);
    }

    public Optional<List<ToolCallFunction>> getTools() {
        return Optional.ofNullable(tools);
    }   

    public void setTools(Optional<List<ToolCallFunction>> tools) {
        this.tools = tools.orElse(null);
    }
}
