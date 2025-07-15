// package configs;
// import graph.Agent;

// import graph.*;
// import graph.TopicManagerSingleton;
// import graph.TopicManagerSingleton.TopicManager;


// public class PlusAgent implements Agent {
//     private double x = Double.NaN, y = Double.NaN;
//     private final String[] subs;
//     private final String[] pubs;
//     private final TopicManager tm = TopicManagerSingleton.get();

//     public PlusAgent(String[] subs, String[] pubs) {
//         this.subs = subs;
//         this.pubs = pubs;

//         if (subs.length >= 2) {
//             tm.getTopic(subs[0]).subscribe(this);
//             tm.getTopic(subs[1]).subscribe(this);
//         }

//         if (pubs.length >= 1) {
//             tm.getTopic(pubs[0]).addPublisher(this);
//         }
//     }

//     @Override
//     public void callback(String topic, Message msg) {
//         if (topic.equals(subs[0]) && !Double.isNaN(msg.asDouble)) {
//             x = msg.asDouble;
//         } else if (topic.equals(subs[1]) && !Double.isNaN(msg.asDouble)) {
//             y = msg.asDouble;
//         }

//         if (!Double.isNaN(x) && !Double.isNaN(y)) {
//             tm.getTopic(pubs[0]).publish(new Message(x + y));
//         }
//     }

//     @Override
//     public String getName() {
//         return "PlusAgent";
//     }

//     @Override
//     public void reset() {
//         x = Double.NaN;
//         y = Double.NaN;
//     }

//     @Override
//     public void close() {
//         // No threads to close
//     }
// }

package configs;

import graph.Agent;
import graph.Message;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class PlusAgent implements Agent {
    private double x = 0.0;
    private double y = 0.0;
    private boolean xSet = false;
    private boolean ySet = false;

    private final String[] subs;
    private final String[] pubs;
    private final TopicManager tm = TopicManagerSingleton.get();

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

    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        xSet = false;
        ySet = false;
        x = 0.0;
        y = 0.0;
    }

    @Override
    public void close() {}
}
