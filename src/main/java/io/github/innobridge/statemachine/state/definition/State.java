package io.github.innobridge.statemachine.state.definition;

import java.util.Map;
import java.util.function.Function;

public interface State {
   void action(); 
   State processing(Map<State, Function<State, State>> states);
   void setId(String id);
   String getId();
   void setInstanceId(String instanceId);
   String getInstanceId();
}
