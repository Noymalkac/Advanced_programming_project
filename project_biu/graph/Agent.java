package graph;

/**
 * Agent.java
 * This file is part of the project_biu graph representation.
 * It defines the Agent interface for agents that can interact with the graph.
 */
public interface Agent {
    /**
     * Gets the name of the agent.
     * @return the name of the agent
     */
    String getName();
    /**
     * Resets the agent's state.
     * This method is called to reset the agent's state, clearing any messages or subscriptions.
     */
    void reset();
    /**
     * Callback method for receiving messages from subscribed topics.
     * This method is called when a message is published to a topic the agent is subscribed to.
     *
     * @param topic The name of the topic the message was published to.
     * @param msg The message that was published.
     */
    void callback(String topic, Message msg);
    /**
     * Closes the agent, releasing any resources it holds.
     * This method is called to clean up the agent's resources when it is no longer needed.
     */
    void close();
}
