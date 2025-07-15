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

public class TopicDisplayer implements Servlet {

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

    @Override
    public void close() {}
}
