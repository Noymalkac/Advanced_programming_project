/**
 * ConfLoader.java
 * This file is part of the project_biu servlet implementation.
 * It handles the loading of configuration files and building HTML representations of graphs.
 */
package servlets;

import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;
import configs.*;
import graph.Graph;
import java.io.*;
import java.util.List;

/**
 * ConfLoader is a servlet that handles the uploading of configuration files
 * and builds a graph representation from the uploaded content.
 */
public class ConfLoader implements Servlet {
    /**
     * Handles the HTTP request to upload a configuration file.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String filename = ri.getParameters().get("filename");

        System.out.println("===> Uploaded filename: " + filename);

        byte[] content = ri.getContent();
        System.out.println("===> Uploaded content length: " + (content != null ? content.length : "null"));

        if (filename != null && content != null) {
            try (FileOutputStream fos = new FileOutputStream("config_files/" + filename.replaceAll("\"", ""))) {
                fos.write(content);
            }
            // Load config and build graph
            GenericConfig config = GenericConfig.load(new ByteArrayInputStream(content));
            Graph graph = new Graph(config);

            
            // String html = GraphHtmlBuilder.buildGraphHtml(graph);
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
    }
    /**
     * closes the servlet, releasing any resources it holds.
     */
    @Override
    public void close() {}
}
