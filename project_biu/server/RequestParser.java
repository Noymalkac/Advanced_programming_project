/**
 * RequestParser.java
 * This file is part of the project_biu server implementation.
 * It handles parsing of HTTP requests, including headers, parameters, and body content.
 */

package server;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * RequestParser is a utility class that parses HTTP requests from a BufferedReader.
 * It extracts the HTTP command, URI, headers, query parameters, and body content.
 */
public class RequestParser {

    /**
     * Parses an HTTP request from the given BufferedReader.
     *
     * @param reader The BufferedReader to read the request from.
     * @return A RequestInfo object containing parsed request details.
     * @throws IOException If an I/O error occurs while reading the request.
     */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        // Step 1: Read request line
        String requestLine = reader.readLine();
        if (requestLine == null) throw new IOException("Empty request");

        String[] requestParts = requestLine.split(" ");
        String httpCommand = requestParts[0];
        String uri = requestParts[1];

        // Step 2: Read headers
        Map<String, String> headers = new HashMap<>();
        String line;
        int contentLength = 0;
        String boundary = null;

        while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
            int colonIdx = line.indexOf(':');
            if (colonIdx != -1) {
                String key = line.substring(0, colonIdx).trim();
                String value = line.substring(colonIdx + 1).trim();
                headers.put(key, value);

                if (key.equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(value);
                } else if (key.equalsIgnoreCase("Content-Type") && value.contains("boundary=")) {
                    int idx = value.indexOf("boundary=");
                    if (idx != -1) {
                        boundary = value.substring(idx + "boundary=".length());
                        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                            boundary = boundary.substring(1, boundary.length() - 1);
                        }
                    }
                }
            }
        }

        // Step 3: Parse query parameters from URI
        Map<String, String> parameters = new HashMap<>();
        int queryIndex = uri.indexOf('?');
        if (queryIndex != -1) {
            String queryString = uri.substring(queryIndex + 1);
            uri = uri.substring(0, queryIndex);  // Remove query from URI
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    parameters.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                }
            }
        }

        // Step 4: If not a multipart POST, return early
        if (!"POST".equalsIgnoreCase(httpCommand) || boundary == null || contentLength == 0) {
            return new RequestInfo(
                    httpCommand,
                    uri,
                    uri.split("/"),
                    parameters,
                    new byte[0]
            );
        }

        // Step 5: Read body content as raw bytes
        char[] charBuffer = new char[contentLength];
        int read = 0;
        while (read < contentLength) {
            int r = reader.read(charBuffer, read, contentLength - read);
            if (r == -1) break;
            read += r;
        }

        String body = new String(charBuffer);
        String boundaryMarker = "--" + boundary;

        String filename = null;
        byte[] fileContent = null;

        String[] parts = body.split(boundaryMarker);
        for (String part : parts) {
            if (part.contains("Content-Disposition") && part.contains("filename=")) {
                int nameIdx = part.indexOf("filename=\"");
                int nameEnd = part.indexOf("\"", nameIdx + 10);
                filename = part.substring(nameIdx + 10, nameEnd);

                int contentStart = part.indexOf("\r\n\r\n") + 4;
                int contentEnd = part.lastIndexOf("\r\n");

                if (contentStart > 0 && contentEnd > contentStart) {
                    String contentStr = part.substring(contentStart, contentEnd);
                    fileContent = contentStr.getBytes("ISO-8859-1"); // Preserve raw bytes
                }
                break;
            }
        }

        if (filename != null) {
            parameters.put("filename", filename);
        }

        return new RequestInfo(
                httpCommand,
                uri,
                uri.split("/"),
                parameters,
                fileContent != null ? fileContent : new byte[0]
        );
    }
    /**
     * Represents the parsed request information.
     */
    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        /**
         * Constructs a RequestInfo object with the parsed request details.
         *
         * @param httpCommand The HTTP command (e.g., GET, POST).
         * @param uri The URI path.
         * @param uriSegments The segments of the URI.
         * @param parameters The query parameters.
         * @param content The body content of the request.
         */
        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }
        /**
         * Gets the HTTP command of the request.
         *
         * @return The HTTP command (e.g., GET, POST).
         */
        public String getHttpCommand() {
            return httpCommand;
        }
        /**
         * Gets the URI of the request.
         *
         * @return The URI path.
         */
        public String getUri() {
            return uri;
        }
        /**
         * Gets the segments of the URI.
         *
         * @return An array of URI segments.
         */
        public String[] getUriSegments() {
            return uriSegments;
        }
        /**
         * Gets the query parameters of the request.
         *
         * @return A map of query parameters.
         */
        public Map<String, String> getParameters() {
            return parameters;
        }
        /**
         * Gets the body content of the request.
         *
         * @return The body content as a byte array.
         */
        public byte[] getContent() {
            return content;
        }
        /**
         * Gets a specific query parameter by key.
         *
         * @param key The key of the query parameter.
         * @return The value of the query parameter, or null if not found.
         */
        public String getParameter(String key) {
            return parameters.get(key);
        }
    }
}
