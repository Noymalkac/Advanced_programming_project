// package configs;

// import graph.TopicManagerSingleton.TopicManager;
// import graph.TopicManagerSingleton;
// // import test.Agent;
// // import test.Message;
// // import test.Topic;
// import graph.Agent;
// import graph.Message;



// public class IncAgent implements Agent {
//     private final String[] subs;
//     private final String[] pubs;
//     private final TopicManager tm = TopicManagerSingleton.get();

//     public IncAgent(String[] subs, String[] pubs) {
//         this.subs = subs;
//         this.pubs = pubs;

//         if (subs.length >= 1) {
//             tm.getTopic(subs[0]).subscribe(this);
//         }

//         if (pubs.length >= 1) {
//             tm.getTopic(pubs[0]).addPublisher(this);
//         }
//     }

//     @Override
//     public void callback(String topic, Message msg) {
//         // if (!Double.isNaN(msg.asDouble)) {
//         if (subs.length >= 1 && topic.equals(subs[0]) && !Double.isNaN(msg.asDouble)) {
//             tm.getTopic(pubs[0]).publish(new Message(msg.asDouble + 1));
//         }
//     }

//     @Override
//     public String getName() {
//         return "IncAgent";
//     }

//     @Override
//     public void reset() {}

//     @Override
//     public void close() {
//         // No threads to close
//     }
// }

package configs;

import graph.TopicManagerSingleton.TopicManager;
import graph.TopicManagerSingleton;
import graph.Agent;
import graph.Message;

public class IncAgent implements Agent {
    private final String[] subs;
    private final String[] pubs;
    private final TopicManager tm = TopicManagerSingleton.get();

    public IncAgent(String[] subs, String[] pubs) {
        this.subs = subs;
        this.pubs = pubs;

        if (subs.length >= 1) {
            tm.getTopic(subs[0]).subscribe(this);
        }

        if (pubs.length >= 1) {
            tm.getTopic(pubs[0]).addPublisher(this);
        }
    }

    @Override
    public void callback(String topic, Message msg) {
        if (subs.length >= 1 && topic.equals(subs[0]) && !Double.isNaN(msg.asDouble)) {
            double result = msg.asDouble + 1.0;
            tm.getTopic(pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {}

    @Override
    public void close() {}
}
