package ex02.pyrmont;

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.ServletOutputStream;

/**
 * ServletResponse javax.servlet-api.jar
 *
 */
public class Response implements ServletResponse {

    private static final int BUFFER_SIZE = 1024;
    
    Request request;
    
    // socket.getOutputStream()
    OutputStream output;
    // 
    PrintWriter writer;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * 静态资源请求处理
     * 
     * @throws IOException
     */
    public void sendStaticResource() throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(Constants.WEB_ROOT, request.getUri());
            fis = new FileInputStream(file);

            int len = fis.available();
            byte[] bytes = new byte[len];
            int ch = fis.read(bytes, 0, len);
            String headers = 
            		  "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + len + "\r\n"
                    + "\r\n";

            if (ch != -1) {
                output.write(headers.getBytes());
                output.write(bytes, 0, ch);
                //System.out.println(Arrays.toString(bytes));
                System.out.println(Thread.currentThread().getName() + " response:\n" + new String(bytes, 0, bytes.length));
            }
        } catch (FileNotFoundException e) {
            String errorMessage = 
            		  "HTTP/1.1 404 File Not Found\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: 23\r\n"
                    + "\r\n"
                    + "<h1>File Not Found</h1>";
            
            System.out.println(Thread.currentThread().getName() + " response:\n" + errorMessage);
            output.write(errorMessage.getBytes());
            
        } finally {
            if (fis != null){
                fis.close();
            }
        }
    }


    /**
     * implementation of ServletResponse
     */

    public PrintWriter getWriter() throws IOException {
        // autoflush is true, println() will flush,
        // but print() will not.
        writer = new PrintWriter(output, true);
        
        System.out.println(Thread.currentThread().getName() + " Response.getWriter() writer=" + writer);
        return writer;
    }

    public void flushBuffer() throws IOException {
    }

    public int getBufferSize() {
        return 0;
    }

    public Locale getLocale() {
        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    public void setCharacterEncoding(String s) {

    }

    public boolean isCommitted() {
        return false;
    }

    public void reset() {
    }

    public void resetBuffer() {
    }

    public void setBufferSize(int size) {
    }

    public void setContentLength(int length) {
    }

    public void setContentType(String type) {
    }

    public void setLocale(Locale locale) {
    }


    public String getCharacterEncoding() {
        return null;
    }

    public String getContentType() {
        return null;
    }
}