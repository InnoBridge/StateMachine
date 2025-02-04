package io.github.innobridge.statemachine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.innobridge.llmtools.models.Message;
import io.github.innobridge.llmtools.models.response.ToolCallFunction;
import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.state.definition.ChatResponseState;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;
import io.github.innobridge.statemachine.state.definition.InitialState;
import io.github.innobridge.statemachine.state.definition.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatService {
    
    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private ExecutionThreadRepository executionThreadRepository;

    public List<Message> toolUse(InitialState initialState, List<ToolCallFunction> tools) {

        Map<String, Object> result = stateMachineService.createToolsStateMachine(
            initialState, 
            tools,
            Optional.empty(),
            Optional.empty());
        
        String threadId = result.get("threadId").toString();

        ExecutionThread thread;
        State currentState;
        
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            thread = executionThreadRepository.findById(threadId).get();
            currentState = stateMachineService.getState(threadId, thread.getCurrentState());
            
        } while (!(currentState instanceof ChatResponseState));

        ChatResponseState chatResponseState = (ChatResponseState) currentState;
        List<String> responses = chatResponseState.getResponses();

        stateMachineService.processStateMachine(
            threadId, 
            Optional.empty(), 
            Optional.empty(), 
            Optional.empty());

        List<Message> messages = new ArrayList<>();
        responses.forEach(response -> {
            messages.add(Message.builder().role("system").content(response).build());
        });
        return messages;
    }
    
}
