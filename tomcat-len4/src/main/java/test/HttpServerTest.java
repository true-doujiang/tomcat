package test;

import org.apache.catalina.util.ParameterMap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
            //out.println("GET /servlet/PrimitiveServlet;jsessionid=ghco9xdnaco31gmafukxchph?username=uuu&info=18 HTTP/1.1");
            out.println("GET /servlet/PrimitiveServlet?username=uuu&info=19&address=上海 HTTP/1.1");
            out.println("Host: localhost:8080");
            out.println("Connection: Close");
            out.println("content-length: 11");
            out.println("content-type: text/html"); //application/x-www-form-urlencoded
            out.println("Cookie: userName=budi;password=pwd");
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
