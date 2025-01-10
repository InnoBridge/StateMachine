package io.github.innobridge.statemachine.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.github.innobridge.statemachine.constants.StateMachineConstant.EXCHANGE_NAME;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.QUEUE_NAME;
import static io.github.innobridge.statemachine.constants.StateMachineConstant.ROUTING_KEY;

@Configuration
public class QueueConfig {

    @Value("${RABBITMQ_HOST:localhost}")
    private String host;

    @Value("${RABBITMQ_PORT:5672}")
    private int port;

    @Value("${RABBITMQ_USERNAME:admin}")
    private String username;

    @Value("${RABBITMQ_PASSWORD:admin}")
    private String password;

    @Value("${RABBITMQ_VIRTUAL_HOST:/}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }
    
    @Bean
    public Queue stateMachineQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange stateMachineExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding stateMachineBinding() {
        return BindingBuilder
                .bind(stateMachineQueue())
                .to(stateMachineExchange())
                .with(ROUTING_KEY);
    }
}
