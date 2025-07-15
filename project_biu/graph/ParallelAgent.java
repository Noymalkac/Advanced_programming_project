/**
 * ParallelAgent.java
 * This file is part of the project_biu graph management system.
 * It implements an Agent that processes messages in parallel using a dedicated worker thread.
 */
package graph;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * ParallelAgent is an implementation of the Agent interface that processes messages in parallel.
 * It uses a blocking queue to handle incoming messages and a worker thread to process them.
 */
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
    /**
     * Constructs a ParallelAgent with a specified queue capacity.
     *
     * @param agent The Agent to delegate message processing to.
     * @param queueCapacity The capacity of the internal message queue.
     */
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
    /**
     * Handles a callback from the agent, putting the message into the queue for processing.
     *
     * @param topic The topic of the message.
     * @param msg The message to process.
     */
    @Override
    public void callback(String topic, Message msg) {
        try {
            queue.put(new TopicMessage(topic, msg)); // waits if full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // good practice
        }
    }
    /**
     * Resets the agent, which in this case resets the underlying agent.
     */
    @Override
    public void reset() {
        agent.reset();
    }
    /**
     * Gets the name of the agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return agent.getName();
    }
    /**
     * Closes the ParallelAgent, stopping the worker thread and cleaning up resources.
     */
    @Override
    public void close() {
        running = false;
        workerThread.interrupt(); // in case it's blocked on take
        agent.close();
    }
}
