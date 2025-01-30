package io.github.innobridge.statemachine.constants;

public class StateMachineConstant {
    // RabbitMQ
    public static final String QUEUE_NAME = "state_machine";
    public static final String EXCHANGE_NAME = "state_machine_exchange";
    public static final String ROUTING_KEY = "state_machine_routing_key";
    
    // Database
    public static final String STATE_MACHINE = "StateMachine";
    public static final String STATE_MACHINE_INSTANCE = "StateMachineInstance";
    public static final String EXECUTION_THREAD = "ExecutionThread";
    public static final String STATES = "States";
    public static final String HISTORY = "History";
}
