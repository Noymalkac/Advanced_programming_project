package configs;

import java.util.function.BinaryOperator;
import java.util.HashMap;
import graph.TopicManagerSingleton;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {
    private final String name;
    private final String in1, in2, out;
    private final BinaryOperator<Double> op;
    private final TopicManager tm;
    private final HashMap<String, Double> inputs = new HashMap<>();

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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        inputs.clear();
    }

    @Override
    public void close() {}
}
