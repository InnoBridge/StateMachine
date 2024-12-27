package io.github.innobridge.statemachine.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StateMachineController {
    
    @GetMapping("/hello")
    public String hello() {

        return "hello world";
    }

}
