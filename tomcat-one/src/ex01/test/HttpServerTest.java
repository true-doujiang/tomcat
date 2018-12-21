package ex01.test;

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
			//socket 使用的是TCP协议  你在这里无论什么参数都是走TCP协议传输
            Socket socket = new Socket("127.0.0.1", 8080);
            //一定要带true
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //GET签名没有/
            out.println("GET /aa.html HTTP/1.1");
            out.println("Host: localhost:8080");
            out.println("Connection: Close");
            out.println();
            out.println("name=yhh&age=18");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream() ) );
            boolean loop = true;
            StringBuffer sb = new StringBuffer(1024);
            while (loop) {
                //循环很多次才会进入if
				System.out.println("loop");
                if ( in.ready() ) {
                    int i=0;
                    while (i!=-1) {
                        i = in.read();
                        sb.append((char) i);
                    }
                    loop = false;
                }
                Thread.sleep(100);
            }
            System.out.println("response:\n" + sb.toString());
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	
    public static void send2() {
        try {
            Socket socket = new Socket("localhost", 8080);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("GET /aa.html HTTP/1.1");
			out.println("Host: localhost:8080");
			out.println("Connection: Close");
			out.println();
			out.println("name=123");

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();

			String crlf = System.getProperty("line.separator");
			boolean loop = true;

			while (loop) {
				System.out.println("loop");
				if (reader.ready()) {
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line);
						sb.append(crlf);
					}
					loop = false;
				}
			}

			System.out.println(sb.toString());
			socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
