/**
 * PlusAgent.java
 * This file is part of the project_biu configuration management system.
 * It implements an Agent that computes the sum of two input values and publishes the result.
 */
package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * PlusAgent.java
 * This file is part of the project_biu configuration management system.
 * It implements an Agent that computes the sum of two input values and publishes the result.
 */
public class PlusAgent implements Agent {
    private double x = 0.0;
    private double y = 0.0;
    private boolean xSet = false;
    private boolean ySet = false;

    private final String[] subs;
    private final String[] pubs;
    private final TopicManager tm = TopicManagerSingleton.get();
    /**
     * Constructs a PlusAgent with specified subscriptions and publications.
     *
     * @param subs An array of topic names to subscribe to.
     * @param pubs An array of topic names to publish results to.
     */
    public PlusAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        if (subs.length >= 2) {
            tm.getTopic(subs[0]).subscribe(this);
            tm.getTopic(subs[1]).subscribe(this);
        }

        if (pubs.length >= 1) {
            tm.getTopic(pubs[0]).addPublisher(this);
        }
    }
    /**
     * Resets the PlusAgent's internal state.
     * This method clears the stored values and flags.
     */
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(subs[0]) && !Double.isNaN(msg.asDouble)) {
            x = msg.asDouble;
            xSet = true;
        } else if (topic.equals(subs[1]) && !Double.isNaN(msg.asDouble)) {
            y = msg.asDouble;
            ySet = true;
        }

        // As soon as any input is updated, compute with known values
        if (xSet || ySet) {
            double result = x + y;
            tm.getTopic(pubs[0]).publish(new Message(result));
        }
    }
    /**
     * Gets the name of the PlusAgent.
     * @return the name of the agent
     */
    @Override
    public String getName() {
        return "PlusAgent";
    }
    
    /**
     * Resets the PlusAgent's internal state.
     * This method clears the stored values and flags.
     */
    @Override
    public void reset() {
        xSet = false;
        ySet = false;
        x = 0.0;
        y = 0.0;
    }
    
    /**
     * Closes the PlusAgent, releasing any resources it holds.
     * This method is called when the agent is no longer needed.
     */
    @Override
    public void close() {}
}
