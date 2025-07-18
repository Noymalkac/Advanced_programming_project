/**
 * HtmlGraphWriter.java
 * This file is part of the project_biu visualization module.
 * It generates HTML and JavaScript code to visualize a graph using the vis.js library.
 */
package views;

import configs.Node;
import graph.Graph;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * HtmlGraphWriter is responsible for generating HTML code that visualizes a graph structure.
 * It uses the vis.js library to create an interactive graph representation.    
 * The generated HTML includes nodes and edges based on the graph's structure,
 * with specific styling for different types of nodes (e.g., topics and agents).    
 */

public class HtmlGraphWriter {
    /**
     * Generates HTML and JavaScript code to visualize the graph using vis.js.
     *
     * @param graph The graph to visualize.
     * @return A list of strings representing the HTML lines with embedded JavaScript for visualization.
     * @throws IOException If an I/O error occurs while reading the HTML template file.
     */
    public static List<String> getGraphHTML(Graph graph) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("html_files/graph.html"));
        StringBuilder script = new StringBuilder();

        script.append("<script>\n");
        script.append("const nodes = [\n");

        for (Node node : graph) {
            String name = node.getName(); // e.g., TA, APlusAgent

            if (name.startsWith("T")) {
                // It's a topic node
                String topicName = name.substring(1);  // remove the T prefix
                Topic topic = TopicManagerSingleton.get().getTopic(topicName);
                Message msg = (topic != null) ? topic.getLastMessage() : null;
                double value = (msg != null) ? msg.getDouble() : 0.0;

                // Combine name + value in one label
                String label = topicName + "\\n" + value;

                script.append(String.format(
                    "{ id: '%s', label: '%s', shape: 'box', color: 'lightgreen' },\n",
                    name, label
                ));
            } else if (name.startsWith("A")) {
                // Agent node
                script.append(String.format(
                    "{ id: '%s', label: '%s', shape: 'circle', color: 'lightblue' },\n",
                    name, name.substring(1)
                ));
            } else {
                // Fallback for unknown types
                script.append(String.format(
                    "{ id: '%s', label: '%s', shape: 'circle', color: 'gray' },\n",
                    name, name
                ));
            }
        }

        script.append("];\n");

        script.append("const edges = [\n");
        for (Node from : graph) {
            for (Node to : from.getEdges()) {
                script.append(String.format(
                    "{ from: '%s', to: '%s', arrows: 'to' },\n",
                    from.getName(), to.getName()
                ));
            }
        }
        script.append("];\n");

        script.append("const container = document.getElementById('graph');\n");
        script.append("const data = { nodes: new vis.DataSet(nodes), edges: new vis.DataSet(edges) };\n");
        script.append("const options = { nodes: { font: { size: 16 } }, edges: { arrows: 'to' }, physics: false };\n");
        script.append("new vis.Network(container, data, options);\n");

        script.append("</script>\n");

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("<!--SCRIPT_HERE-->")) {
                result.add(script.toString());
            } else {
                result.add(line);
            }
        }

        return result;
    }
}  