package io.github.innobridge.statemachine.state.definition;

import java.util.Map;

public interface ChildToolState extends ChildState {

    Map<String, Class<? extends InitialState>> registerChildInstanceMap();
    
}
