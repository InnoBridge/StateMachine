package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.Optional;

public interface TerminalState extends State {

    Optional<Map<String, Object>> getPayload(); 

}
