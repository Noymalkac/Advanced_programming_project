/**
 * HtmlLoader.java
 * This file is part of the project_biu servlet implementation.
 * It handles loading and serving HTML files based on HTTP requests.
 */
package servlets;

import server.RequestParser.RequestInfo;
import java.io.*;

/** * HtmlLoader is a servlet that serves HTML files from a specified folder.
 * It reads the requested file and sends it back to the client, or returns a 404 error if the file is not found.
 */
public class HtmlLoader implements Servlet {
    private final String htmlFolder;
    /**
     * Constructor for HtmlLoader.
     *
     * @param htmlFolder The folder where HTML files are stored.
     */
    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }
    /**
     * Handles the HTTP request to load an HTML file.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        System.out.println("===> HtmlLoader received request for URI: " + ri.getUri());

        String[] segments = ri.getUriSegments();
        String fileName = segments.length > 0 ? segments[segments.length - 1] : "index.html";

        System.out.println("===> Extracted fileName: " + fileName);

        File file = new File(htmlFolder, fileName);
        System.out.println("===> Resolved file path: " + file.getAbsolutePath());

        PrintWriter pw = new PrintWriter(toClient, true);

        if (file.exists()) {
            System.out.println("===> File found. Sending response...");

            pw.println("HTTP/1.1 200 OK");
            pw.println("Content-Type: text/html");
            pw.println();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pw.println(line);
                }
            }
        } else {
            System.out.println("===> File not found: " + file.getAbsolutePath());

            pw.println("HTTP/1.1 404 Not Found");
            pw.println("Content-Type: text/html");
            pw.println();
            pw.println("<html><body><h3>File not found: " + fileName + "</h3></body></html>");
        }
    }
    /**
     * Closes the servlet, releasing any resources it holds.
     * In this case, there are no resources to release, so this method is empty.
     */
    @Override
    public void close() {}
}
