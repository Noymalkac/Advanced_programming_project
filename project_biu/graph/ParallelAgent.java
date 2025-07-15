package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ParallelAgent implements Agent {
    private final Agent agent;
    private final BlockingQueue<TopicMessage> queue;
    private final Thread workerThread;
    private volatile boolean running = true;

    // Internal helper class to wrap topic and message together
    private static class TopicMessage {
        String topic;
        Message message;

        TopicMessage(String topic, Message message) {
            this.topic = topic;
            this.message = message;
        }
    }

    public ParallelAgent(Agent agent, int queueCapacity) {
        this.agent = agent;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);

        this.workerThread = new Thread(() -> {
            try {
                while (running) {
                    TopicMessage tm = queue.take(); // waits if empty
                    agent.callback(tm.topic, tm.message);
                }
            } catch (InterruptedException e) {
                // Exit thread when interrupted during take()
                Thread.currentThread().interrupt();
            }
        });
        workerThread.start();
    }

    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new TopicMessage(topic, msg)); // waits if full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // good practice
        }
    }

    @Override
    public void reset() {
        agent.reset();
    }

    @Override
    public String getName() {
        return agent.getName();
    }

    @Override
    public void close() {
        running = false;
        workerThread.interrupt(); // in case it's blocked on take
        agent.close();
    }
}
