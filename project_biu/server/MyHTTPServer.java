/**
 * MyHTTPServer.java
 * This file is part of the project_biu server implementation.
 * It handles HTTP requests and manages servlets for different HTTP commands.
 */

package server;

import server.RequestParser.RequestInfo;

import servlets.Servlet;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * MyHTTPServer is a custom HTTP server implementation that listens for incoming HTTP requests,
 * manages servlets for different HTTP commands, and handles client connections in a thread pool.
 */
public class MyHTTPServer extends Thread implements HTTPServer {
    private final int port;
    private final ExecutorService pool;
    private final Map<String, Map<String, Servlet>> servletMap;
    private volatile boolean running = true;
    private ServerSocket serverSocket;

    /**
     * Constructor for MyHTTPServer.
     *
     * @param port The port number on which the server will listen.
     * @param nThreads The number of threads in the thread pool for handling requests.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.pool = Executors.newFixedThreadPool(nThreads);
        this.servletMap = new HashMap<>();
        servletMap.put("GET", new ConcurrentHashMap<>());
        servletMap.put("POST", new ConcurrentHashMap<>());
        servletMap.put("DELETE", new ConcurrentHashMap<>());
    }
    /**
     * Adds a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST).
     * @param uri The URI path for which the servlet should handle requests.
     * @param s The servlet instance to handle requests.
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        Map<String, Servlet> map = servletMap.get(httpCommand.toUpperCase());
        if (map != null) {
            map.put(uri, s);
        }
    }
    /**
     * Removes a servlet for a specific HTTP command and URI.
     *
     * @param httpCommand The HTTP command (e.g., GET, POST).
     * @param uri The URI path for which the servlet should be removed.
     */
    public void removeServlet(String httpCommand, String uri) {
        Map<String, Servlet> map = servletMap.get(httpCommand.toUpperCase());
        if (map != null) {
            map.remove(uri);
        }
    }
    /**
     * Starts the HTTP server.
     */
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
    /**
     * Handles incoming client requests.
     *
     * @param client The client socket to handle.
     */
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
    /**
     * Closes the server and releases resources.
     */
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
