package io.github.innobridge.statemachine.consumer;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.QUEUE_NAME;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.CHILD_QUEUE_NAME;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DuplicateKeyException;

import io.github.innobridge.statemachine.service.StateMachineService;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class RabbitMQConsumer {
    
    @Autowired
    private StateMachineService stateMachineService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @RabbitListener(queues = {CHILD_QUEUE_NAME})
    public void consumeChildMessage(String message) {
        log.info("Received child message: " + message);
        try {
            Map<String, Object> messageMap = objectMapper.readValue(message, Map.class);
            String parentId = (String) messageMap.get("parentId");
            String childId = (String) messageMap.get("childId");
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) messageMap.get("payload");

            if (parentId == null || childId == null) {
                log.error("Invalid child message format. Missing parentId or childId: {}", message);
                return;
            }

            if (payload != null) {
                stateMachineService.processChildStateMachine(parentId, childId, Optional.of(payload));
            } else {
                stateMachineService.processChildStateMachine(parentId, childId, Optional.empty());
            }
        } catch (Exception e) {
            log.error("Failed to process child state machine. Message: {}, Error: {}", message, e.getMessage());
            // Do not rethrow - this prevents message requeuing
        }
    }
}
