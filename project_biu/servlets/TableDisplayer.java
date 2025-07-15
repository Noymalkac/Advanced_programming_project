package servlets;

import graph.Topic;
import graph.TopicManagerSingleton;
import graph.Message;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class TableDisplayer implements Servlet {
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

    @Override
    public void close() {}
}
