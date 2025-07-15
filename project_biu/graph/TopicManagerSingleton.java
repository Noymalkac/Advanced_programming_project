package graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TopicManagerSingleton {

    public static class TopicManager {
        private final ConcurrentMap<String, Topic> topicMap = new ConcurrentHashMap<>();

        private TopicManager() {}

        public Topic getTopic(String name) {
            return topicMap.computeIfAbsent(name, Topic::new);
        }

        public Collection<Topic> getTopics() {
            return topicMap.values();
        }

        public void clear() {
            topicMap.clear();
        }

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

    public static TopicManager get() {
        return instance;
    }
}
