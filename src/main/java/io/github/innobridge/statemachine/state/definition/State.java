package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

public interface State {
   void action(Optional<JsonNode> input); 
   State transition(Map<String, Function<State, State>> transitions);
   State processing(Map<String, Function<State, State>> states, Optional<JsonNode> input);
   default State processing(Map<String, Function<State, State>> states) {
       return processing(states, Optional.empty());
   }
   void setId(String id);
   String getId();
   void setInstanceId(String instanceId);
   String getInstanceId();
   boolean isBlocking();
   void setBlocking(boolean blocking);
}
