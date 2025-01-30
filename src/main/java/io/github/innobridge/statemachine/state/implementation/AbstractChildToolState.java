package io.github.innobridge.statemachine.state.implementation;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.innobridge.llmtools.models.response.ToolCallFunction;
import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.service.StateMachineService;
import io.github.innobridge.statemachine.state.definition.ChildToolState;
import io.github.innobridge.statemachine.state.definition.ConfigurableState;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.STATES;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Document(collection = STATES)
public abstract class AbstractChildToolState extends AbstractChildState implements ChildToolState {

    public AbstractChildToolState() {
        super();
        setBlocking(false);
        setDispatched(false);
    }

    @Override
    public State processing(Map<String, Function<State, State>> transitions, 
    Optional<JsonNode> input, 
    ExecutionThreadRepository executionThreadRepository, 
    StateMachineService stateMachineService) {
        if (!isDispatched()) {
            ExecutionThread thread = executionThreadRepository.findById(getInstanceId()).get();
            List<ToolCallFunction> tools = thread.getTools().get();
            setChildIds(dispatch(stateMachineService, tools));
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

    private Set<String> dispatch(StateMachineService stateMachineService, List<ToolCallFunction> tools) {
        Set<String> childIds = new HashSet<>();
        Map<String, Class<? extends InitialState>> registerChildInstanceMap = registerChildInstanceMap();

        tools.stream().forEach(tool -> {
            String toolName = tool.getName();
            Map<String, Object> arguments = tool.getArguments();
            try {
                Class<? extends InitialState> initialStateClass = registerChildInstanceMap.get(toolName);
                InitialState initialState = initialStateClass.getConstructor(Map.class).newInstance(arguments);
                childIds.add(stateMachineService.createToolsStateMachine(initialState, tools, Optional.empty(), Optional.of(instanceId)).get("threadId").toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return childIds;
    }

    @Override
    public List<InitialState> registerChildInstances() {
        throw new UnsupportedOperationException("This operation is not supported");   
    }

    private boolean completedChildInstances(Set<String> childIds, ExecutionThreadRepository executionThreadRepository) {
        return !executionThreadRepository.existsByIdIn(childIds);
    }

    private void setChildIds(Set<String> childIds) {
        this.childIds = childIds;
    }

}
