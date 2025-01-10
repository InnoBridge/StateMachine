package io.github.innobridge.statemachine.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.EXCHANGE_NAME;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.ROUTING_KEY;

@Slf4j
@Service
public class RabbitMQProducer {
    
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        log.info(String.format("Message sent -> %s", message));
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
    }
    
}
