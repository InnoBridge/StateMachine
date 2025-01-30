package io.github.innobridge.statemachine.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.innobridge.llmtools.models.response.ToolCallFunction;
import io.github.innobridge.statemachine.state.definition.InitialState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatService {
    
    @Autowired
    private StateMachineService stateMachineService;

    public void toolUse(InitialState initialState, List<ToolCallFunction> tools) {

        Map<String, Object> result = stateMachineService.createToolsStateMachine(
            initialState, 
            tools,
            Optional.empty(),
            Optional.empty());

        System.out.println(result);
    }
    
}
