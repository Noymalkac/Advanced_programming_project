// package server;

// import java.io.BufferedReader;
// // import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;

// public class RequestParser {

//     public static RequestInfo parseRequest(InputStream input) throws IOException {
//     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//     String requestLine = reader.readLine();
//     if (requestLine == null) throw new IOException("Empty request");

//     String[] parts = requestLine.split(" ");
//     String httpCommand = parts[0];
//     String uri = parts[1];

//     Map<String, String> parameters = new HashMap<>();
//     String boundary = null;
//     int contentLength = 0;

//     // Read headers
//     String line;
//     while ((line = reader.readLine()) != null && !line.isEmpty()) {
//         if (line.startsWith("Content-Type: multipart/form-data")) {
//             int idx = line.indexOf("boundary=");
//             if (idx != -1) boundary = line.substring(idx + 9).trim();
//         }
//         if (line.startsWith("Content-Length:")) {
//             contentLength = Integer.parseInt(line.substring(15).trim());
//         }
//     }

//     if (boundary == null) throw new IOException("No multipart boundary found");

//     // Read raw body
//     byte[] body = new byte[contentLength];
//     int read = 0;
//     while (read < contentLength) {
//         int r = input.read(body, read, contentLength - read);
//         if (r == -1) break;
//         read += r;
//     }

//     // Extract filename
//     int filenameStart = indexOf(body, "filename=\"".getBytes()) + 10;
//     int filenameEnd = indexOf(body, "\"".getBytes(), filenameStart);
//     String filename = new String(Arrays.copyOfRange(body, filenameStart, filenameEnd));

//     // Extract content
//     int fileStart = indexOf(body, "\r\n\r\n".getBytes(), filenameEnd) + 4;
//     int boundaryPos = indexOf(body, ("--" + boundary).getBytes(), fileStart);
//     int fileEnd = boundaryPos - 4;

//     byte[] content = Arrays.copyOfRange(body, fileStart, fileEnd);
//     parameters.put("filename", filename);

//     return new RequestInfo(httpCommand, uri, new String[]{}, parameters, content);
// }

// private static int indexOf(byte[] array, byte[] target) {
//     return indexOf(array, target, 0);
// }

// private static int indexOf(byte[] array, byte[] target, int fromIndex) {
//     outer: for (int i = fromIndex; i <= array.length - target.length; i++) {
//         for (int j = 0; j < target.length; j++) {
//             if (array[i + j] != target[j]) continue outer;
//         }
//         return i;
//     }
//     return -1;
// }

    
//     // RequestInfo given internal class
//     public static class RequestInfo {
//         private final String httpCommand;
//         private final String uri;
//         private final String[] uriSegments;
//         private final Map<String, String> parameters;
//         private final byte[] content;

//         public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
//             this.httpCommand = httpCommand;
//             this.uri = uri;
//             this.uriSegments = uriSegments;
//             this.parameters = parameters;
//             this.content = content;
//         }

//         public String getHttpCommand() {
//             return httpCommand;
//         }

//         public String getUri() {
//             return uri;
//         }

//         public String[] getUriSegments() {
//             return uriSegments;
//         }

//         public Map<String, String> getParameters() {
//             return parameters;
//         }

//         public byte[] getContent() {
//             return content;
//         }
//     }
// }








// package server;

// // import java.io.InputStream;
// import java.io.BufferedReader;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;

// public class RequestParser {

//     public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
//         String firstLine = reader.readLine(); // e.g., "GET /path?x=1 HTTP/1.1"
//         if (firstLine == null) return null;

//         String[] parts = firstLine.split(" ");
//         String httpCommand = parts[0];
//         String uri = parts[1];

//         Map<String, String> parameters = new HashMap<>();

//         // Extract parameters from URI
//         String path = uri;
//         int queryIdx = uri.indexOf('?');
//         if (queryIdx != -1) {
//             path = uri.substring(0, queryIdx);
//             String query = uri.substring(queryIdx + 1);
//             String[] paramPairs = query.split("&");
//             for (String pair : paramPairs) {
//                 String[] kv = pair.split("=");
//                 if (kv.length == 2) {
//                     parameters.put(kv[0], kv[1]);
//                 }
//             }
//         }

//         // Split URI segments
//         String[] uriSegments = Arrays.stream(path.split("/"))
//                                     .filter(s -> !s.isEmpty())
//                                     .toArray(String[]::new);

//         // Read headers
//         int contentLength = 0;
//         String line;
//         while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
//             String[] header = line.split(":", 2);
//             if (header.length == 2) {
//                 if (header[0].trim().equalsIgnoreCase("Content-Length")) {
//                     contentLength = Integer.parseInt(header[1].trim());
//                 }
//             }
//         }

//         // Read parameter-like lines (e.g., filename="hello.txt")
//         while (reader.ready() && (line = reader.readLine()) != null && !line.trim().isEmpty()) {
//             String[] kv = line.split("=", 2);
//             if (kv.length == 2) {
//                 parameters.put(kv[0].trim(), kv[1].trim());
//             }
//         }

//         // // Read content
//         // ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
//         // for (int i = 0; i < contentLength; i++) {
//         //     int ch = reader.read();
//         //     if (ch == -1) break;
//         //     contentStream.write(ch);
//         // }
//         // byte[] content = contentStream.toByteArray();

//         ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
//         while (reader.ready() && (line = reader.readLine()) != null && !line.isEmpty()) {
//             contentStream.write(line.getBytes());
//             contentStream.write('\n');
//         }
        
//         byte[] content = contentStream.toByteArray();

//         return new RequestInfo(httpCommand, uri, uriSegments, parameters, content);
//     }


//     // Your existing nested class
//     public static class RequestInfo {
//         private final String httpCommand;
//         private final String uri;
//         private final String[] uriSegments;
//         private final Map<String, String> parameters;
//         private final byte[] content;

//         public RequestInfo(String httpCommand, String uri, String[] uriSegments,
//                            Map<String, String> parameters, byte[] content) {
//             this.httpCommand = httpCommand;
//             this.uri = uri;
//             this.uriSegments = uriSegments;
//             this.parameters = parameters;
//             this.content = content;
//         }

//         public String getHttpCommand() {
//             return httpCommand;
//         }

//         public String getUri() {
//             return uri;
//         }

//         public String[] getUriSegments() {
//             return uriSegments;
//         }

//         public Map<String, String> getParameters() {
//             return parameters;
//         }

//         public byte[] getContent() {
//             return content;
//         }
//     }
// }






// package server;

// import java.io.*;
// import java.util.*;

// public class RequestParser {

//     public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
//         // Step 1: Read request line
//         String requestLine = reader.readLine();
//         if (requestLine == null) throw new IOException("Empty request");

//         String[] requestParts = requestLine.split(" ");
//         String httpCommand = requestParts[0];
//         String uri = requestParts[1];

//         Map<String, String> headers = new HashMap<>();
//         String line;
//         int contentLength = 0;
//         String boundary = null;

//         // Step 2: Read headers
//         while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
//             int colonIdx = line.indexOf(':');
//             if (colonIdx != -1) {
//                 String key = line.substring(0, colonIdx).trim();
//                 String value = line.substring(colonIdx + 1).trim();
//                 headers.put(key, value);

//                 if (key.equalsIgnoreCase("Content-Length")) {
//                     contentLength = Integer.parseInt(value);
//                 } else if (key.equalsIgnoreCase("Content-Type") && value.contains("boundary=")) {
//                     int idx = value.indexOf("boundary=");
//                     if (idx != -1) {
//                         boundary = value.substring(idx + "boundary=".length());
//                         if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
//                             boundary = boundary.substring(1, boundary.length() - 1);
//                         }
//                     }
//                 }
//             }
//         }

//         // Step 3: Fallback for non-multipart GET/POST
//         if (!"POST".equalsIgnoreCase(httpCommand) || boundary == null || contentLength == 0) {
//             return new RequestInfo(
//                     httpCommand,
//                     uri,
//                     uri.split("/"),
//                     new HashMap<>(),
//                     new byte[0]
//             );
//         }

//         // Step 4: Read body as raw bytes
//         char[] charBuffer = new char[contentLength];
//         int read = 0;
//         while (read < contentLength) {
//             int r = reader.read(charBuffer, read, contentLength - read);
//             if (r == -1) break;
//             read += r;
//         }

//         String body = new String(charBuffer);
//         String boundaryMarker = "--" + boundary;

//         String filename = null;
//         byte[] fileContent = null;

//         String[] parts = body.split(boundaryMarker);
//         for (String part : parts) {
//             if (part.contains("Content-Disposition") && part.contains("filename=")) {
//                 int nameIdx = part.indexOf("filename=\"");
//                 int nameEnd = part.indexOf("\"", nameIdx + 10);
//                 filename = part.substring(nameIdx + 10, nameEnd);

//                 int contentStart = part.indexOf("\r\n\r\n") + 4;
//                 int contentEnd = part.lastIndexOf("\r\n");

//                 if (contentStart > 0 && contentEnd > contentStart) {
//                     String contentStr = part.substring(contentStart, contentEnd);
//                     fileContent = contentStr.getBytes("ISO-8859-1"); // Preserve raw bytes
//                 }
//                 break;
//             }
//         }

//         Map<String, String> parameters = new HashMap<>();
//         if (filename != null) {
//             parameters.put("filename", filename);
//         }

//         return new RequestInfo(
//                 httpCommand,
//                 uri,
//                 uri.split("/"),
//                 parameters,
//                 fileContent != null ? fileContent : new byte[0]
//         );
//     }

//     // RequestInfo class already provided
//     public static class RequestInfo {
//         private final String httpCommand;
//         private final String uri;
//         private final String[] uriSegments;
//         private final Map<String, String> parameters;
//         private final byte[] content;

//         public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
//             this.httpCommand = httpCommand;
//             this.uri = uri;
//             this.uriSegments = uriSegments;
//             this.parameters = parameters;
//             this.content = content;
//         }

//         public String getHttpCommand() {
//             return httpCommand;
//         }

//         public String getUri() {
//             return uri;
//         }

//         public String[] getUriSegments() {
//             return uriSegments;
//         }

//         public Map<String, String> getParameters() {
//             return parameters;
//         }

//         public byte[] getContent() {
//             return content;
//         }

//         public String getParameter(String key) {
//             return parameters.get(key);
//         }
//     }
// }

package server;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

public class RequestParser {

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

    public static class RequestInfo {
        private final String httpCommand;
        private final String uri;
        private final String[] uriSegments;
        private final Map<String, String> parameters;
        private final byte[] content;

        public RequestInfo(String httpCommand, String uri, String[] uriSegments, Map<String, String> parameters, byte[] content) {
            this.httpCommand = httpCommand;
            this.uri = uri;
            this.uriSegments = uriSegments;
            this.parameters = parameters;
            this.content = content;
        }

        public String getHttpCommand() {
            return httpCommand;
        }

        public String getUri() {
            return uri;
        }

        public String[] getUriSegments() {
            return uriSegments;
        }

        public Map<String, String> getParameters() {
            return parameters;
        }

        public byte[] getContent() {
            return content;
        }

        public String getParameter(String key) {
            return parameters.get(key);
        }
    }
}
