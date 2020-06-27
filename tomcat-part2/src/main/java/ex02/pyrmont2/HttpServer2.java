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

    /**
     * 本应用servlet容器仅能运行实现了javax.servlet.Servlet接口的Servlet
     * 其实也能运行实现了javax.servlet.GenericServlet接口的Servlet
     * 不能运行实现HttpServlet接口的Servlet是因为
     *   service()中做了判断  传入的req、resp 必须实现HttpServletRequest HttpServletResponse接口
     *   if (!(req instanceof HttpServletRequest &&
            res instanceof HttpServletResponse)) {
            throw new ServletException("non-HTTP request or response");
         }
     * @param args
     */
    public static void main(String[] args) {
        HttpServer2 server = new HttpServer2();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 3000, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName() + " System.exit(1)2");
            System.exit(1);
        }

//		try {
//			System.out.println(serverSocket.getSoTimeout());
//			System.out.println(serverSocket.getReceiveBufferSize());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
        
		// Loop waiting for a request
        while (!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
            	System.out.println(Thread.currentThread().getName() + " serverSocket accept() 准备就绪");
                socket = serverSocket.accept();
                count++;
                System.out.println(Thread.currentThread().getName() + " 新客户端链接 socket" + count + " = " + socket);
                //Thread.sleep(1000);

                input = socket.getInputStream();
                System.out.println("input " + input + " 可读len = " + input.available());
                
                output = socket.getOutputStream();
                System.out.println("output = " + output);
                
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
