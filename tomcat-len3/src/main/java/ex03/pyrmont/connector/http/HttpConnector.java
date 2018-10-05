package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 作者: 尤欢欢
 * 日期： 2018年8月12日 下午1:03:15
 * 描述： 创建一个服务端套接字 并 等待HTTP请求
 */
public class HttpConnector implements Runnable {

    boolean stopped;
    private String scheme = "http";

    public String getScheme() {
        return scheme;
    }

    public void run() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        while (!stopped) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (Exception e) {
                continue;
            }
            
            System.out.println(Thread.currentThread().getName() + " 新客户端接入: socket = " + socket);
            
            /**
             * 每次HttpConnector实例只有一个HttpProcessor实例可以使用，所以每次他只能处理一个HTTP请求
             */
            // 创建Request和Response对象          本应用中this并没有用到
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket);
        }
    }

    public void start() {
        Thread thread = new Thread(this, "thread-HttpConnector");
        thread.start();
    }
}