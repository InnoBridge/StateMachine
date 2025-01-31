package io.github.innobridge.statemachine.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.HashMap;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.CHILD_ROUTING_KEY;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.EXCHANGE_NAME;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.ROUTING_KEY;

@Service
@Slf4j
public class RabbitMQProducer {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(String message) {
        log.info(String.format("Message sent -> %s", message));
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }

    public void sendChildMessage(String parentId, String childId) {
        sendChildMessage(parentId, childId, null);
    }

    public void sendChildMessage(String parentId, String childId, Map<String, Object> payload) {
        Map<String, Object> message = new HashMap<>();
        message.put("parentId", parentId);
        message.put("childId", childId);
        if (payload != null) {
            message.put("payload", payload);
        }
        
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            log.info("Child message sent -> Parent: {}, Child: {}, Payload: {}", parentId, childId, payload);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, CHILD_ROUTING_KEY, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to serialize message", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
