package ex02.pyrmont2;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class HttpServer2 {

    private int count;

    // shutdown command
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

    // the shutdown command received
    private boolean shutdown = false;

    public static void main(String[] args) {
        HttpServer2 server = new HttpServer2();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 3, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName() + " System.exit(1)2");
            System.exit(1);
        }

        // Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                socket = serverSocket.accept();
                count++;
                System.out.println(Thread.currentThread().getName() + " 新客户端链接 socket" + count + " = " + socket);
                //Thread.sleep(1000);

                input = socket.getInputStream();
                output = socket.getOutputStream();

                Request request = new Request(input);
                request.parse();

                Response response = new Response(output);
                response.setRequest(request);


                if (request.getUri().startsWith("/servlet/")) {
                    System.out.println(Thread.currentThread().getName() + " 处理--servlet--请求");
                    ServletProcessor2 processor = new ServletProcessor2();
                    processor.process(request, response);
                } else {
                    System.out.println(Thread.currentThread().getName() + " 处理--静态资源--请求");
                    StaticResourceProcessor processor = new StaticResourceProcessor();
                    processor.process(request, response);
                }

                socket.close();
                System.out.println(Thread.currentThread().getName() + " 关闭 socket" + count + " = " + socket + "\n");
                if (request.getUri() != null) {
                    shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(Thread.currentThread().getName() + " System.exit(1)22");
                //System.exit(1);
            }
        }
    }
}
