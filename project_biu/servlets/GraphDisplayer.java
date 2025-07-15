package servlets;

import graph.Graph;
import views.HtmlGraphWriter;

import java.io.*;
import java.util.List;

public class GraphDisplayer implements Servlet {
    @Override
    public void handle(server.RequestParser.RequestInfo ri, OutputStream toClient) throws IOException {
        Graph graph = new Graph(null);
        List<String> htmlLines = HtmlGraphWriter.getGraphHTML(graph);

        PrintWriter pw = new PrintWriter(toClient, true);
        pw.println("HTTP/1.1 200 OK");
        pw.println("Content-Type: text/html");
        pw.println();

        for (String line : htmlLines) {
            pw.println(line);
        }
        pw.flush();
    }

    @Override
    public void close() {}
}
