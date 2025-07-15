/**
 * TableDisplayer.java
 * This file is part of the project_biu servlet implementation.
 * It handles the display of a table showing the last values of topics.
 */
package servlets;

import graph.Topic;
import graph.TopicManagerSingleton;
import graph.Message;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/** * TableDisplayer is a servlet that generates an HTML table displaying the last values of all topics.
 * It retrieves the last message for each topic and formats it into a table structure.
 */
public class TableDisplayer implements Servlet {
    /**
     * Handles the HTTP request to display a table of last topic values.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(server.RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        PrintWriter pw = new PrintWriter(toClient, true);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: text/html");
        pw.println();

        pw.println("<html><head><meta charset='UTF-8'>");
        pw.println("<style>");
        pw.println("body { font-family: sans-serif; }");
        pw.println("h3 { font-size: 22px; color: #2c3e50; }");
        pw.println("table { border-collapse: collapse; width: 100%; }");
        pw.println("th, td { border: 1px solid #999; padding: 6px 12px; text-align: left; }");
        pw.println("th { background-color: #f2f2f2; font-weight: bold; }");
        pw.println("</style></head><body>");
        pw.println("<br>");
        pw.println("<h3> Last Topic Values</h3>");
        pw.println("<table>");
        pw.println("<tr><th>Topic</th><th>Last Value</th></tr>");
 
        Map<String, String> lastValues = new TreeMap<>();
        for (Topic t : TopicManagerSingleton.get().getTopics()) {
            Message m = t.getLastMessage();
            lastValues.put(t.name, m != null ? m.getText() : "0.0");
        }
        for (Map.Entry<String, String> entry : lastValues.entrySet()) {
            pw.println("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
        }
        pw.println("</table></body></html>");
        pw.flush();
    }
    /**
     * Closes the servlet, releasing any resources it holds.
     * In this case, there are no resources to release, so this method is empty.
     */
    @Override
    public void close() {}
}
