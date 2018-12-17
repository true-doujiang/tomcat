package ex02.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpServerTest {

    public static void main(String[] args) {

        send();
    }

    public static void send() {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // HTTP/0.8  也可以
            out.println("GET /PrimitiveServlet;jsessionid=khdslah?username=uuu&info=18 HTTP/1.1");
            out.println("Host: localhost:8080");
            out.println("Connection: Close");
            out.println();
            out.println("name=yhh&age=18");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream() ) );
            boolean loop = true;
            StringBuffer sb = new StringBuffer(1024);
            while (loop) {
                if ( in.ready() ) {
                    int i=0;
                    while (i!=-1) {
                        i = in.read();
                        sb.append((char) i);
                    }
                    loop = false;
                }
                //Thread.currentThread().sleep(100);
            }
            System.out.println("response:\n" + sb.toString());
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
