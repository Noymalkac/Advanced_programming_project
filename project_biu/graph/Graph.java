/**
 * Graph.java
 * This file is part of the project_biu graph management system.
 * It represents a directed graph of nodes, where each node can represent a topic or an agent.
 */
package graph;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import graph.TopicManagerSingleton.TopicManager;
import configs.GenericConfig;
import configs.Node;
/**
 * Graph is a collection of nodes representing topics and agents.
 * It provides methods to create the graph from topics, check for cycles, and manage nodes.
 */
public class Graph extends ArrayList<Node> {
    /**
     * Constructs a new graph from the given config.
     * @param config The configuration object defining the graph structure.
     */
    public Graph(GenericConfig config) {
        this.createFromTopics();  // builds from TopicManager
    }
    /**
     * Checks if the graph has cycles.
     *
     * @return true if the graph has cycles, false otherwise.
     */
    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) {
                return true;
            }
        }
        return false;
    }
    /**
     * Clears the graph and removes all nodes.
     */
    public void createFromTopics() {
        Map<String, Node> nodes = new HashMap<>();
        TopicManager tm = TopicManagerSingleton.get();

        for (Topic t : tm.getTopics()) {
            // System.out.println("Found topic: " + t.name);
            String topicName = "T" + t.name;
            nodes.putIfAbsent(topicName, new Node(topicName));
            Node topicNode = nodes.get(topicName);

            for (Agent a : t.getSubscribers()) {
                String agentName = "A" + a.getName();
                nodes.putIfAbsent(agentName, new Node(agentName));
                topicNode.addEdge(nodes.get(agentName));
            }
        }

        for (Topic t : tm.getTopics()) {
            for (Agent a : t.getPublishers()) {
                String agentName = "A" + a.getName();
                String topicName = "T" + t.name;

                nodes.putIfAbsent(agentName, new Node(agentName));
                nodes.putIfAbsent(topicName, new Node(topicName));
                Node agentNode = nodes.get(agentName);
                Node topicNode = nodes.get(topicName);

                agentNode.addEdge(topicNode);
            }
        }

        this.clear();
        this.addAll(nodes.values());
    }
}
