/**
 * BinOpAgent.java
 * This file is part of the project_biu configuration management system.
 * It implements a binary operation agent that subscribes to two input topics,
 * performs a binary operation on their values, and publishes the result to an output topic.
 */
package configs;

import java.util.function.BinaryOperator;
import java.util.HashMap;
import graph.TopicManagerSingleton;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton.TopicManager;

/**
 * BinOpAgent is an agent that performs a binary operation on the last values of two input topics
 * and publishes the result to an output topic.
 * It subscribes to the input topics and listens for messages to perform the operation.
 */
public class BinOpAgent implements Agent {
    private final String name;
    private final String in1, in2, out;
    private final BinaryOperator<Double> op;
    private final TopicManager tm;
    private final HashMap<String, Double> inputs = new HashMap<>();
    /**
     * Constructs a BinOpAgent with specified input and output topics and a binary operation.
     *
     * @param name The name of the agent.
     * @param in1 The first input topic name.
     * @param in2 The second input topic name.
     * @param out The output topic name.
     * @param op The binary operation to perform on the input values.
     */
    public BinOpAgent(String name, String in1, String in2, String out, BinaryOperator<Double> op) {
        this.name = name;
        this.in1 = in1;
        this.in2 = in2;
        this.out = out;
        this.op = op;
        this.tm = TopicManagerSingleton.get();

        tm.getTopic(in1).subscribe(this);
        tm.getTopic(in2).subscribe(this);
        tm.getTopic(out).addPublisher(this);

    }
    /**
     * Handles a callback from the agent, storing input values and publishing the result
     * when both inputs are available.
     *
     * @param topic The topic of the message.
     * @param msg The message to process.
     */
    @Override
    public void callback(String topic, Message msg) {
        if (!Double.isNaN(msg.asDouble)) {
            inputs.put(topic, msg.asDouble);
        }
        if (inputs.containsKey(in1) && inputs.containsKey(in2)) {
            double result = op.apply(inputs.get(in1), inputs.get(in2));
            tm.getTopic(out).publish(new Message(result));
            inputs.clear();
        }
    }
    /**
     * Returns the name of the agent.
     *
     * @return The name of the agent.
     */
    @Override
    public String getName() {
        return name;
    }
    /**
     * Resets the agent's internal state.
     * This method clears the stored input values.
     */
    @Override
    public void reset() {
        inputs.clear();
    }
    /**
     * Closes the agent, releasing any resources it holds.
     * This method is a no-op in this implementation.
     */
    @Override
    public void close() {}
}
