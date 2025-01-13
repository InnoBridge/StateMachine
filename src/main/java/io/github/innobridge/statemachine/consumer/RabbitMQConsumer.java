package io.github.innobridge.statemachine.consumer;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.QUEUE_NAME;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;

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
        try {
            stateMachineService.processStateMachine(message);
        } catch (IllegalStateException | DuplicateKeyException e) {
            log.error("Failed to process state machine for thread {}: {}", message, e.getMessage());
            // Do not rethrow - this prevents message requeuing
        }
    }
}
