package ex02.pyrmont;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * 本应用程序中的 servlet容器只能执行PrimitiveServlet
 */
public class HttpServer1 {

    // shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // the shutdown command received
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer1 server = new HttpServer1();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 3, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName() + " System.exit(1)1");
            System.exit(1);
        }

        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                //阻塞等待HTTP请求
                socket = serverSocket.accept();
                System.out.println(Thread.currentThread().getName() + " 新客户端链接 socket = " + socket);

                input = socket.getInputStream();
                output = socket.getOutputStream();

                Request request = new Request(input);
                request.parse();

                Response response = new Response(output);
                response.setRequest(request);

                if (request.getUri() != null && request.getUri().startsWith("/servlet/")) {
                    System.out.println(Thread.currentThread().getName() + " 处理--servlet--请求");

                    ServletProcessor1 processor = new ServletProcessor1();
                    processor.process(request, response);
                } else {
                    System.out.println(Thread.currentThread().getName() + " 处理--静态资源--请求");

                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                socket.close();
                System.out.println(Thread.currentThread().getName() + " 关闭 socket = " + socket);

                if (request.getUri() != null) {
                    shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " System.exit(1)11");
                System.exit(1);
            }
        }
    }
}
