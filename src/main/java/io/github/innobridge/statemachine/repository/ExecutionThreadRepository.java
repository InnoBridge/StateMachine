package io.github.innobridge.statemachine.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.innobridge.statemachine.state.definition.ExecutionThread;

public interface ExecutionThreadRepository extends MongoRepository<ExecutionThread, String> {
    
}
