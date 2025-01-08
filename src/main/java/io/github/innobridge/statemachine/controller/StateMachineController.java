package io.github.innobridge.statemachine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.innobridge.statemachine.service.StateMachineService;
import io.github.innobridge.statemachine.state.usecases.InitialHelloWorld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class StateMachineController {

    @Autowired
    private StateMachineService stateMachineService;

    @GetMapping("/hello")
    public String hello() {

        return "hello world";
    }

    @PostMapping("/create/helloworld")
    public String createHelloWorld() {
        return stateMachineService.createStateMachine(new InitialHelloWorld());
    }

    @PostMapping("/process")
    public String processStateMachine(String instanceId) {
        return stateMachineService.processStateMachine(instanceId);
    }
}
