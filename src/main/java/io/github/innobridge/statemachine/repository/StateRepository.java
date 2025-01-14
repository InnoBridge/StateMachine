package io.github.innobridge.statemachine.repository;

import io.github.innobridge.statemachine.state.implementation.AbstractState;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StateRepository extends MongoRepository<AbstractState, String> {
    // You can add custom query methods here if needed
    // For example:
    // List<AbstractState> findByType(String type);
    List<AbstractState> findByInstanceId(String instanceId);
    @Query("{ 'instanceId' : ?0, '_class' : ?1 }")
    List<AbstractState> findByInstanceIdAndClass(String instanceId, String className);

    @Query(value = "{ 'instanceId' : ?0 }", delete = true)
    void deleteByInstanceId(String instanceId);
}
