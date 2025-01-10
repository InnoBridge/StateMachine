package io.github.innobridge.statemachine.consumer;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.QUEUE_NAME;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.innobridge.statemachine.service.StateMachineService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RabbitMQConsumer {
    
    @Autowired
    private StateMachineService stateMachineService;

    @RabbitListener(queues = {QUEUE_NAME})
    public void consumeMessage(String message) {
        log.info("Received message: " + message);
        stateMachineService.processStateMachine(message);
    }
}
