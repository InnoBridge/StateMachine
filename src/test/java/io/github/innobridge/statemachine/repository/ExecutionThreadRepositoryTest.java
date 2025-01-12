package io.github.innobridge.statemachine.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;


import io.github.innobridge.statemachine.state.definition.ExecutionThread;

@DataMongoTest
public class ExecutionThreadRepositoryTest {

    @Autowired
    private ExecutionThreadRepository executionThreadRepository;

    @Test
    void shouldSaveExecutionThread() {
        // Given
        ExecutionThread thread = new ExecutionThread();
        String threadId = UUID.randomUUID().toString();
        thread.setId(threadId);
        thread.setInstanceType("TEST_TYPE");
        thread.setParentId(Optional.empty());
        thread.setCurrentState("INITIAL");

        // When
        ExecutionThread savedThread = executionThreadRepository.save(thread);

        // Then
        assertThat(savedThread).isNotNull();
        assertThat(savedThread.getId()).isEqualTo(threadId);
        assertThat(savedThread.getInstanceType()).isEqualTo("TEST_TYPE");
        assertThat(savedThread.getParentId()).isEmpty();
        assertThat(savedThread.getCurrentState()).isEqualTo("INITIAL");

        // Verify we can retrieve it from the database
        Optional<ExecutionThread> retrievedThread = executionThreadRepository.findById(threadId);
        assertThat(retrievedThread).isPresent();
        assertThat(retrievedThread.get().getId()).isEqualTo(threadId);
    }
}
