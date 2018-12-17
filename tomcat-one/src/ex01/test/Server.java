package ex01.test;

import java.io.*;
import java.net.*;

public class Server {


	////参考  ServerSocket讲解    https://blog.csdn.net/tian779278804/article/details/50922354/
	
    private int port = 8000;
    private ServerSocket serverSocket;

    public Server() throws IOException {
        //连接请求队列的长度为3
        serverSocket = new ServerSocket(port, 3);
        System.out.println("服务器启动");
    }

    public void service() {
        while (true) {
            Socket socket = null;
            try {
                //从连接请求队列中取出一个连接
                socket = serverSocket.accept();
                System.out.println("New connection accepted " + socket.getInetAddress() + ":" + socket.getPort());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) throws Exception {
        Server server = new Server();
        //睡眠10分钟
        Thread.sleep(60000 * 10);
        //server.service();
    }
}
