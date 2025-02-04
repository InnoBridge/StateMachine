package io.github.innobridge.statemachine.state.definition;

import java.util.List;

public interface ChatResponseState extends BlockingTransitionState {

    List<String> getResponses();
    
}
