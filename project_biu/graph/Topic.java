/**
 * Topic.java
 * This file is part of the project_biu graph management system.
 * It defines the Topic class, which represents a topic in the system,
 * allowing agents to subscribe, publish messages, and manage subscribers and publishers.
 */
package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a topic in the graph system.
 * Topics can have subscribers and publishers, and they can publish messages to all subscribers.
 */
public class Topic {
    /** The name of the topic. */
    public final String name;
    private final List<Agent> subs = new ArrayList<>();
    private final List<Agent> pubs = new ArrayList<>();
    private Message lastMessage;
    /**
     * Constructs a Topic with the specified name.
     *
     * @param name The name of the topic.
     */
    Topic(String name) {
        this.name = name;
    }
    /**
     * subscribes an agent to the topic.
     * @param a The agent to subscribe.
     * If the agent is already subscribed, it will not be added again.
     */
    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }
    /**
     * Unsubscribes an agent from the topic.
     *
     * @param a The agent to unsubscribe.
     */
    public void unsubscribe(Agent a) {
        subs.remove(a);
    }
    /**
     * Publishes a message to the topic, notifying all subscribers.
     *
     * @param m The message to publish.
     */
    public void publish(Message m) {
        this.lastMessage = m; // <-- Store the message
        for (Agent a : subs) {
            a.callback(name, m);
        }
    }
    /**
     * Adds a publisher to the topic.
     *
     * @param a The agent to add as a publisher.
     */
    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }
    /**
     * Removes a publisher from the topic.
     *
     * @param a The agent to remove as a publisher.
     */
    public void removePublisher(Agent a) {
        pubs.remove(a);
    }
    /**
     * Gets the name of the topic.
     *
     * @return The name of the topic.
     */
    public List<Agent> getSubscribers() {
        return subs;
    }

    /**
     * Gets the list of publishers for this topic.
     *
     * @return A list of agents that are publishers for this topic.
     */
    public List<Agent> getPublishers() {
        return pubs;
    }

    // Returns the last message published to this topic
    // or null if no messages have been published yet.
    /**
     * Returns the last message published to this topic.
     *
     * @return The last message, or null if no messages have been published yet.
     */
    public Message getLastMessage() {
        return lastMessage;
    }

}
