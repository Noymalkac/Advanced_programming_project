import server.HTTPServer;
import server.MyHTTPServer;
import servlets.ConfLoader;
import servlets.HtmlLoader;
import servlets.TopicDisplayer;
import java.awt.Desktop;
import java.net.URI;



public class Main {
public static void main(String[] args) throws Exception{
    
    HTTPServer server=new MyHTTPServer(8080,5);
    server.addServlet("GET", "/publish", new TopicDisplayer());
    server.addServlet("POST", "/upload", new ConfLoader());
    server.addServlet("GET", "/app/", new HtmlLoader("html_files"));
    server.addServlet("GET", "/graph", new servlets.GraphDisplayer());
    server.addServlet("GET", "/table", new servlets.TableDisplayer());
    server.start();
    // Automatically open browser on Windows
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(new URI("http://localhost:8080/app/index.html"));
    }

    System.in.read();
    server.close();
    System.out.println("done");
    }
}
