// package server;

// import server.RequestParser.RequestInfo;

// import java.io.*;
// import java.net.*;
// import java.util.*;
// import java.util.concurrent.*;
// import servlets.Servlet;

// public class MyHTTPServer extends Thread implements HTTPServer {

//     private final int port;
//     private volatile boolean running = true;
//     private final ExecutorService threadPool;
//     private final Map<String, Map<String, Servlet>> servlets;

//     private ServerSocket serverSocket;

//     public MyHTTPServer(int port, int nThreads) {
//         this.port = port;
//         this.threadPool = Executors.newFixedThreadPool(nThreads);
//         this.servlets = new HashMap<>();
//         servlets.put("GET", new ConcurrentHashMap<>());
//         servlets.put("POST", new ConcurrentHashMap<>());
//         servlets.put("DELETE", new ConcurrentHashMap<>());
//     }

//     public void addServlet(String httpCommand, String uri, Servlet s) {


//         // System.out.println("Registered [" + httpCommand.toUpperCase() + "] " + uri);
//         // if (httpCommand == null || uri == null || s == null) {
//         //     throw new IllegalArgumentException("HTTP command, URI, and servlet cannot be null");
//         // }


//         Map<String, Servlet> map = servlets.get(httpCommand.toUpperCase());
//         if (map != null) {
//             map.put(uri, s);
//         }
//     }

//     public void removeServlet(String httpCommand, String uri) {
//         Map<String, Servlet> map = servlets.get(httpCommand.toUpperCase());
//         if (map != null) {
//             map.remove(uri);
//         }
//     }

//     public void run() {
//         try {
//             serverSocket = new ServerSocket(port);
//             while (running) {
//                 try {
//                     Socket clientSocket = serverSocket.accept();
//                     threadPool.submit(() -> handleClient(clientSocket));
//                 } catch (SocketException e) {
//                     if (running) e.printStackTrace(); // Ignore if closing
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }

//     private void handleClient(Socket clientSocket) {
//         try (
//             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//             OutputStream out = clientSocket.getOutputStream()
//         ) {
//             RequestInfo ri = RequestParser.parseRequest(in);

//             String cmd = ri.getHttpCommand().toUpperCase();
//             String uri = ri.getUri();

//             Map<String, Servlet> map = servlets.get(cmd);
//             Servlet matched = null;
//             int maxMatch = -1;

//             if (map != null) {
//                 for (String path : map.keySet()) {
//                     if (uri.startsWith(path) && path.length() > maxMatch) {
//                         matched = map.get(path);
//                         maxMatch = path.length();
//                     }
//                 }
//             }

//             if (matched != null) {
//                 matched.handle(ri, out);
//             } else {
//                 PrintWriter pw = new PrintWriter(out, true);
//                 pw.println("HTTP/1.1 404 Not Found");
//                 pw.println("Content-Type: text/plain");
//                 pw.println();
//                 pw.println("No matching servlet found for URI: " + uri);
//             }

//         } catch (Exception e) {
//             e.printStackTrace();
//         } finally {
//             try {
//                 clientSocket.close();
//             } catch (IOException e) {}
//         }
//     }

//     public void close() {
//         running = false;
//         try {
//             if (serverSocket != null)
//                 serverSocket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         threadPool.shutdownNow();
//         for (Map<String, Servlet> m : servlets.values()) {
//             for (Servlet s : m.values()) {
//                 try {
//                     s.close();
//                 } catch (IOException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }
//     }


// }


package server;

import server.RequestParser.RequestInfo;

import servlets.Servlet;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final ExecutorService pool;
    private final Map<String, Map<String, Servlet>> servletMap;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(nThreads);
        this.servletMap = new HashMap<>();
        servletMap.put("GET", new ConcurrentHashMap<>());
        servletMap.put("POST", new ConcurrentHashMap<>());
        servletMap.put("DELETE", new ConcurrentHashMap<>());
    }

    public void addServlet(String httpCommand, String uri, Servlet s) {
        Map<String, Servlet> map = servletMap.get(httpCommand.toUpperCase());
        if (map != null) {
            map.put(uri, s);
        }
    }

    public void removeServlet(String httpCommand, String uri) {
        Map<String, Servlet> map = servletMap.get(httpCommand.toUpperCase());
        if (map != null) {
            map.remove(uri);
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    pool.submit(() -> handleClient(client));
                } catch (SocketException se) {
                    if (running) se.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket client) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            OutputStream out = client.getOutputStream();
        ) {

            RequestInfo ri = RequestParser.parseRequest(in);
            if (ri == null) return;

            String cmd = ri.getHttpCommand().toUpperCase();
            // String uri = ri.getUri();
            String uri = ri.getUri().split("\\?")[0];  // Strip query parameters
            Map<String, Servlet> uriMap = servletMap.get(cmd);

            System.out.println("===> Incoming request: " + cmd + " " + uri);///////////////////////////////

            Servlet bestMatch = null;
            int maxLength = -1;
            if (uriMap != null) {
                for (String path : uriMap.keySet()) {
                    if (uri.startsWith(path) && path.length() > maxLength) {
                        bestMatch = uriMap.get(path);
                        maxLength = path.length();
                    }
                }
            }

            if (bestMatch != null) {
                bestMatch.handle(ri, out);
            } else {
                PrintWriter pw = new PrintWriter(out, true);
                pw.println("HTTP/1.1 404 Not Found");
                pw.println("Content-Type: text/plain");
                pw.println();
                pw.println("No matching servlet for: " + uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { client.close(); } catch (IOException ignored) {}
        }
    }

    public void close() {
        running = false;
        try { serverSocket.close(); } catch (IOException ignored) {}
        pool.shutdownNow();
        for (Map<String, Servlet> map : servletMap.values()) {
            for (Servlet s : map.values()) {
                try { s.close(); } catch (IOException ignored) {}
            }
        }
    }
}
