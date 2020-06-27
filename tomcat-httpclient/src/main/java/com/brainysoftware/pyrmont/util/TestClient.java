package com.brainysoftware.pyrmont.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {

    public static void main(String[] args) {

        for (int i =0; i< 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    send();
                }
            }).start();
        }
    }

    public static void send() {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
            OutputStream os = socket.getOutputStream();
            boolean autoflush = true;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
            //PrimitiveServlet    ModernServlet
//            String message = "GET /aa/servlet/yy?name=yhh HTTP/1.1";
            String message = "GET /servlet/PrimitiveServlet HTTP/1.1";
            out.println(message);

            out.println("Host: localhost:8080");
            out.println("Connection: Close");
            out.println();

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
            System.out.println(sb.toString());
            socket.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
