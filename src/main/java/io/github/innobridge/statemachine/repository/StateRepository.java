package io.github.innobridge.statemachine.repository;

import io.github.innobridge.statemachine.state.implementation.AbstractState;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface StateRepository extends MongoRepository<AbstractState, String> {
    // You can add custom query methods here if needed
    // For example:
    // List<AbstractState> findByType(String type);
    List<AbstractState> findByInstanceId(String instanceId);
    List<AbstractState> findByInstanceIdAndClass(String instanceId, String _class);
}
