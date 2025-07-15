/**
 * TopicDisplayer.java
 * This file is part of the project_biu servlet implementation.
 * It handles the publishing of messages to topics and displays the graph and table of topics.
 */
package servlets;

import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.Topic;
import graph.Message;
import graph.Graph;
import views.HtmlGraphWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

/**
 * TopicDisplayer is a servlet that handles HTTP requests to publish messages to topics
 * and displays the graph and table of topics with their last values.
 */
public class TopicDisplayer implements Servlet {
    /**
     * Handles the HTTP request to publish a message to a topic and display the graph and table.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        System.out.println("===> TopicDisplayer invoked");

        String topicName = ri.getParameters().get("topic");
        String message = ri.getParameters().get("message");

        if (topicName == null || topicName.trim().isEmpty()) {
            sendErrorWithGraphAndTable(toClient, "Topic name is missing or empty.");
            return;
        }

        double numericValue;
        try {
            numericValue = Double.parseDouble(message);
        } catch (NumberFormatException e) {
            sendErrorWithGraphAndTable(toClient, "Invalid numeric value: " + message);
            return;
        }

        Topic topic = TopicManagerSingleton.get().getTopic(topicName);
        if (!topic.getPublishers().isEmpty()) {
            sendErrorWithGraphAndTable(toClient, "Topic '" + topicName + "' is downstream in the graph and cannot be published to directly.");
            return;
        }

        Message msg = new Message(String.valueOf(numericValue));
        topic.publish(msg);
        System.out.println("Published " + message + " to topic: " + topicName);

        Graph graph = new Graph(null);
        List<String> htmlLines = HtmlGraphWriter.getGraphHTML(graph);

        PrintWriter pw = new PrintWriter(toClient, true);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: text/html");
        pw.println();
        for (String line : htmlLines) {
            pw.println(line);
        }

        pw.println("<hr><table border='1'><tr><th>Topic</th><th>Last Value</th></tr>");
        Map<String, String> lastValues = new TreeMap<>();
        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            Message m = t.getLastMessage();
            lastValues.put(t.name, m != null ? m.getText() : "0.0");
        }
        for (Map.Entry<String, String> entry : lastValues.entrySet()) {
            pw.println("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
        }
        pw.println("</table>");
        pw.println("</body></html>");
        pw.flush();
    }

    private void sendErrorWithGraphAndTable(OutputStream toClient, String errorMsg) throws IOException {
        Graph graph = new Graph(null);
        List<String> htmlLines = HtmlGraphWriter.getGraphHTML(graph);

        PrintWriter pw = new PrintWriter(toClient, true);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: text/html");
        pw.println();

        for (String line : htmlLines) {
            pw.println(line);
        }

        pw.println("<hr><p style='color:red; font-weight:bold;'>" + errorMsg + "</p>");
        pw.println("<table border='1'><tr><th>Topic</th><th>Last Value</th></tr>");
        Map<String, String> lastValues = new TreeMap<>();
        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            Message m = t.getLastMessage();
            lastValues.put(t.name, m != null ? m.getText() : "0.0");
        }
        for (Map.Entry<String, String> entry : lastValues.entrySet()) {
            pw.println("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
        }
        pw.println("</table>");
        pw.println("</body></html>");
        pw.flush();
    }
    /**
     * Closes the servlet, releasing any resources it holds.
     * In this case, there are no resources to release, so this method is empty.
     */
    @Override
    public void close() {}
}
