/**
 * Node.java
 * This file is part of the project_biu configuration management system.
 * It implements a Node class that represents a node in a graph structure.
 */
package configs;

import java.util.ArrayList;
import java.util.List;
import graph.Message;

/** * Node.java
 * This file is part of the project_biu configuration management system.
 * It implements a Node class that represents a node in a graph structure.
 */
public class Node {
    private String name;
    private List<Node> edges = new ArrayList<>(); //make sure the new
    private Message msg;

    /**
     * Constructs a Node with the specified name.
     *
     * @param name The name of the node.
     */
    public Node(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the node.
     * @return the name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node.
     * @param name the new name for the node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the edges of the node.
     * @return a list of nodes that this node is connected to
     */
    public List<Node> getEdges() {
        return edges;
    }

    /**
     * Sets the edges of the node.
     * @param edges a list of nodes to set as edges for this node
     */
    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    /**
     * Gets the message associated with the node.
     * @return the message of this node
     */
    public Message getMessage() {
        return msg;
    }
    
    
    /**
     * Sets the message associated with the node.
     * @param msg the message to set for this node
     */
    public void setMessage(Message msg) {
        this.msg = msg;
    }

    /**
     * Adds an edge to another node.
     * @param node the node to connect to this node
     */
    public void addEdge(Node node) {
        // edges.add(node);
        if (!edges.contains(node)) {
            edges.add(node);
        }
    }
    /**
     * Checks if this node has cycles in its edges.
     * @return true if there are cycles, false otherwise
     */
    public boolean hasCycles() {
        return hasCycles(new ArrayList<>());
    }

    private boolean hasCycles(List<Node> visited) {
        if (visited.contains(this)) return true;
        visited.add(this);
        for (Node n : edges) {
            if (n.hasCycles(new ArrayList<>(visited))) {
                return true;
            }
        }
        return false;
    }
}
