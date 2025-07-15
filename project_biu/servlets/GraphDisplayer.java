/**
 * GraphDisplayer.java
 * This file is part of the project_biu servlet implementation.
 * It generates an HTML page to visualize the graph structure using the HtmlGraphWriter.
 */
package servlets;

import graph.Graph;
import views.HtmlGraphWriter;

import java.io.*;
import java.util.List;

/**
 * GraphDisplayer is a servlet that generates an HTML page to visualize the graph structure.
 * It uses the HtmlGraphWriter to create the necessary HTML and JavaScript for visualization.
 */
public class GraphDisplayer implements Servlet {
    /**
     * Handles the HTTP request to display the graph.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
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
    /**
     * Closes the servlet, releasing any resources it holds.
     */
    @Override
    public void close() {}
}
