package ex01.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

  /**
   *
   */
  public static final String WEB_ROOT = System.getProperty("user.dir") + "/src/main/webapp/webroot";

  // shutdown command
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // the shutdown command received
  private boolean shutdown = false;

  public static void main(String[] args) {
    //System.out.println(System.getProperty("user.dir")); //   /..../tomcat1
    HttpServer server = new HttpServer();
    server.await();
  }

  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1); //非0 非正常退出
    }

    // Loop waiting for a request
    while (!shutdown) {
      Socket socket = null;
      InputStream input = null;
      OutputStream output = null;
      try {
        socket = serverSocket.accept();
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // create Request object and parse
        Request request = new Request(input);
        request.parse();

        // create Response object
        Response response = new Response(output);
        response.setRequest(request);
        response.sendStaticResource();

        // Close the socket
        socket.close();

        //check if the previous URI is a shutdown command
        //shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
    }
  }
}
