package graph;

import java.util.ArrayList;
import java.util.List;

public class Topic {
    public final String name;
    private final List<Agent> subs = new ArrayList<>();
    private final List<Agent> pubs = new ArrayList<>();
    private Message lastMessage;

    Topic(String name) {
        this.name = name;
    }

    public void subscribe(Agent a) {
        if (!subs.contains(a)) {
            subs.add(a);
        }
    }

    public void unsubscribe(Agent a) {
        subs.remove(a);
    }

    public void publish(Message m) {
        this.lastMessage = m; // <-- Store the message
        for (Agent a : subs) {
            a.callback(name, m);
        }
    }

    public void addPublisher(Agent a) {
        if (!pubs.contains(a)) {
            pubs.add(a);
        }
    }

    public void removePublisher(Agent a) {
        pubs.remove(a);
    }

        public List<Agent> getSubscribers() {
        return subs;
    }

    public List<Agent> getPublishers() {
        return pubs;
    }

    // Returns the last message published to this topic
    // or null if no messages have been published yet.
    public Message getLastMessage() {
        return lastMessage;
    }

}
