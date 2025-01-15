package io.github.innobridge.statemachine.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.innobridge.statemachine.repository.ExecutionThreadRepository;
import io.github.innobridge.statemachine.repository.StateRepository;
import io.github.innobridge.statemachine.state.definition.ExecutionThread;

@Service
public class VisibilityService {
    
    @Autowired
    private ExecutionThreadRepository executionThreadRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Set<String> getActiveInstances() {
        return executionThreadRepository.findAll().stream()
                .map(ExecutionThread::getId)
                .collect(Collectors.toSet());
    }

    public ExecutionThread getExecutionThread(String instanceId) {
        return executionThreadRepository.findById(instanceId).get();
    }

    public Set<JsonNode> getStates(String instanceId) {
        return stateRepository.findByInstanceId(instanceId).stream()
                .map(state -> (JsonNode) objectMapper.valueToTree(state))
                .collect(Collectors.toSet());
    }
}
