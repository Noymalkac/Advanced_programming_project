package servlets;

import java.io.IOException;
import java.io.OutputStream;

import server.RequestParser.RequestInfo;
/**
 * Servlet interface defines the contract for servlets in the project.
 * It includes methods to handle HTTP requests and to close resources.
 */
public interface Servlet {
    /**
     * Handles the HTTP request.
     *
     * @param ri The RequestInfo object containing request details.
     * @param toClient The OutputStream to write the response to the client.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    /**
     * Closes the servlet, releasing any resources it holds.
     * This method should be called when the servlet is no longer needed.
     *
     * @throws IOException If an I/O error occurs while closing the servlet.
     */
    void close() throws IOException;
}
