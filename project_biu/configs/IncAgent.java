/**
 * IncAgent.java
 * This file is part of the project_biu configuration management system.
 * It implements an incremental agent that subscribes to a topic, increments the received value, and publishes the result.
 */

package configs;

import graph.TopicManagerSingleton.TopicManager;
import graph.TopicManagerSingleton;
import graph.Agent;
import graph.Message;

/** * IncAgent is an agent that subscribes to a topic, increments the received value by 1,
 * and publishes the result to another topic.
 * It is used for testing and demonstration purposes.
 */
public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private final TopicManager tm = TopicManagerSingleton.get();
    /**
     * Constructs an IncAgent with specified subscriptions and publications.
     *
     * @param subs An array of topic names to subscribe to.
     * @param pubs An array of topic names to publish results to.
     */
    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        if (subs.length >= 1) {
            tm.getTopic(subs[0]).subscribe(this);
        }

        if (pubs.length >= 1) {
            tm.getTopic(pubs[0]).addPublisher(this);
        }
    }
    /**
     * Handles a callback from the agent, increments the received value, and publishes the result.
     *
     * @param topic The topic of the message.
     * @param msg The message to process.
     */
    @Override
    public void callback(String topic, Message msg) {
        if (subs.length >= 1 && topic.equals(subs[0]) && !Double.isNaN(msg.asDouble)) {
            double result = msg.asDouble + 1.0;
            tm.getTopic(pubs[0]).publish(new Message(result));
        }
    }
    /**
     * Returns the name of the agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return "IncAgent";
    }
    /**
     * Resets the agent's state.
     * This method is called to clear any stored values or states in the agent.
     */
    @Override
    public void reset() {}

    /**
    * Closes the agent, releasing any resources it holds.
    * This method is a no-op in this implementation.
    */
    @Override
    public void close() {}
}
