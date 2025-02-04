package io.github.innobridge.statemachine.state.definition;

import java.util.Map;

/**
 * Interface for states that can be configured with arguments after instantiation
 */
public interface ConfigurableState {
    void configure(Map<String, Object> arguments);
}
