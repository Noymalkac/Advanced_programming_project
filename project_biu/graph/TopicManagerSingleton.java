/**
 * TopicManagerSingleton.java
 * This file is part of the project_biu graph management system.
 * It provides a singleton instance of TopicManager to manage topics and their messages.
 */
package graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TopicManagerSingleton provides a singleton instance of TopicManager.
 * It allows for managing topics, subscribing agents, and publishing messages.
 */
public class TopicManagerSingleton {
    /**
     * TopicManager is a singleton class that manages topics and their messages.
     * It allows for creating topics, subscribing agents, and publishing messages.
     */
    public static class TopicManager {
        private final ConcurrentMap<String, Topic> topicMap = new ConcurrentHashMap<>();

        private TopicManager() {}
        /**
         * Returns the topic with the given name.
         * @param name The name of the topic to retrieve.
         * @return The corresponding Topic instance, or null if not found.
         */
        public Topic getTopic(String name) {
            return topicMap.computeIfAbsent(name, Topic::new);
        }

        /**
         * Returns a collection of all topics managed by this TopicManager.
         *
         * @return A collection of all topics.
         */
        public Collection<Topic> getTopics() {
            return topicMap.values();
        }
        /**
         * Clears all topics from the TopicManager.
         * This method removes all topics and their associated messages.
         */
        public void clear() {
            topicMap.clear();
        }
        /**
         * Retrieves the most recent message value (as a String) for each topic.
         *
         * @return A map from topic names to their last published string values.
         */
        public Map<String, String> getLastValues() {
            Map<String, String> result = new HashMap<>();
            for (Map.Entry<String, Topic> entry : topicMap.entrySet()) {
                String topicName = entry.getKey();
                Topic topic = entry.getValue();
                Message msg = topic.getLastMessage();
                if (msg != null) {
                    result.put(topicName, msg.getText());  // or msg.getDouble() if preferred
                } else {
                    result.put(topicName, "—");
                }
            }
            return result;
        }
        
    }

    private static final TopicManager instance = new TopicManager();
    /**
     * Returns the singleton instance of TopicManager.
     *
     * @return The singleton instance of TopicManager.
     */
    public static TopicManager get() {
        return instance;
    }
}
