package ex01.pyrmont;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.util.Arrays;


public class Response {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        System.out.println();
        System.out.println("------sendStaticResource start ------");
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if (file.exists()) {
                fis = new FileInputStream(file);
                int len = fis.available();

                byte[] bytes = new byte[len];
                int ch = fis.read(bytes, 0, len);

                if (ch != -1) {
                    String header = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/html\r\n"
                            + "Content-Length: 25\r\n"
                            + "Content-uuuu: uuuu\r\n"   //自定义响应头
                            //+ "Set-Cookie: name=value\r\n"
                            ;

                    output.write(header.getBytes());
                    output.write(bytes, 0, ch);
                    //System.out.println(Arrays.toString(bytes));
                    System.out.println("response:\n" + new String(bytes, 0, bytes.length));
                }
            } else {
                // file not found
                String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                        + "Content-Type: text/html\r\n"
                        + "Content-Length: 25\r\n"
                        + "Content-uuuu: uuuu\r\n"   //自定义响应头
                        //+ "Set-Cookie: name=value\r\n"
                        + "\r\n" + "<h1>File Not Found 77</h1>";
                System.out.println(errorMessage);
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        } finally {
            System.out.println("------sendStaticResource end ------");
            if (fis != null) {
                fis.close();
            }
        }
    }
}