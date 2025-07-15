package servlets;

import server.RequestParser.RequestInfo;
import views.HtmlGraphWriter;
import configs.*;
import graph.Graph;
import java.io.*;
import java.util.List;

public class ConfLoader implements Servlet {
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

    @Override
    public void close() {}
}
