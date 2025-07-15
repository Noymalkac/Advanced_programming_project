package server;
import servlets.Servlet;

/**
 * HTTPServer.java
 * This file is part of the project_biu HTTP server interface.
 * It defines the HTTPServer interface for managing servlets and handling HTTP requests.
 */
public interface HTTPServer extends Runnable{
    /**
     * @param httpCommanmd The HTTP command (e.g., GET, POST).
     * @param uri The URI pattern.
     * @param s The servlet to handle requests.
     */
    public void addServlet(String httpCommanmd, String uri, Servlet s);
    /**
     * @param httpCommanmd The HTTP command (e.g., GET, POST).
     * @param uri The URI to unregister.
     */
    public void removeServlet(String httpCommanmd, String uri);
    /**
     * Starts the HTTP server.
     * This method initializes the server and begins listening for incoming requests.
     */
    public void start();
    /**
     * Stops the HTTP server.
     * This method gracefully shuts down the server, closing all connections and releasing resources.
     */
    public void close();
}
