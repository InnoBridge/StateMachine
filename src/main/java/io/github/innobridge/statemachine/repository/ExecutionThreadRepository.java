package io.github.innobridge.statemachine.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.github.innobridge.statemachine.state.definition.ExecutionThread;

import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionThreadRepository extends MongoRepository<ExecutionThread, String> {
    
    boolean existsByIdIn(Iterable<String> ids);
}
