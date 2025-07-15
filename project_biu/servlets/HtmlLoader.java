// package servlets;

// import server.RequestParser.RequestInfo;
// // import servlets.Servlet;
// import java.io.*;

// public class HtmlLoader implements Servlet {
//     private final String htmlFolder;

//     public HtmlLoader(String htmlFolder) {
//         this.htmlFolder = htmlFolder;
//     }

//     @Override
//     public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
//         String[] segments = ri.getUriSegments();
//         String fileName = segments.length > 0 ? segments[segments.length - 1] : "index.html";
//         File file = new File(htmlFolder, fileName);
//         PrintWriter pw = new PrintWriter(toClient, true);
//         if (file.exists()) {
//             pw.println("HTTP/1.1 200 OK");
//             pw.println("Content-Type: text/html");
//             pw.println();
//             try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//                 String line;
//                 while ((line = br.readLine()) != null) {
//                     pw.println(line);
//                 }
//             }
//         } else {
//             pw.println("HTTP/1.1 404 Not Found");
//             pw.println("Content-Type: text/html");
//             pw.println();
//             pw.println("<html><body><h3>File not found: " + fileName + "</h3></body></html>");
//         }
//     }

//     @Override
//     public void close() {}
// }

package servlets;

import server.RequestParser.RequestInfo;

import java.io.*;

public class HtmlLoader implements Servlet {
    private final String htmlFolder;

    public HtmlLoader(String htmlFolder) {
        this.htmlFolder = htmlFolder;
    }

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


    @Override
    public void close() {}
}
