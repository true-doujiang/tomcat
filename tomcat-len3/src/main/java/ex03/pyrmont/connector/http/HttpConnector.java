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

    int count;

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
            	System.out.println(Thread.currentThread().getName() + " serverSocket accept() 准备就绪");
            	// 业务处理也是用的这个线程  所以一次只能处理一个请求
                socket = serverSocket.accept();
            } catch (Exception e) {
                continue;
            }
            
            count++;
            System.out.println(Thread.currentThread().getName() + " 新客户端接入: socket" + count + " = " + socket);
            
            
            // 每个请求都有一个HttpProcessor  创建Request和Response对象          本应用中this并没有用到  
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket, count);
        }
    }

    public void start() {
        Thread thread = new Thread(this, "thread-HttpConnector");
        thread.start();
    }
}