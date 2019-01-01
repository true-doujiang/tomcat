package ex05.pyrmont.valves;

import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;


/**
 *
 */
public class HeaderLoggerValve implements Valve, Contained {

    protected Container container;


    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }


    public void invoke(Request request, Response response, ValveContext valveContext)
            throws IOException, ServletException {

        // Pass this request on to the next valve in our pipeline
        // 先调用下一个阀 本阀里的任务实际上还没执行呢
        valveContext.invokeNext(request, response);

        System.out.println("Header Logger Valve");

        ServletRequest sreq = request.getRequest();
        if (sreq instanceof HttpServletRequest) {
            HttpServletRequest hreq = (HttpServletRequest) sreq;
            Enumeration headerNames = hreq.getHeaderNames();

            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement().toString();
                String headerValue = hreq.getHeader(headerName);
                System.out.println(headerName + ":" + headerValue);
            }
        } else {
            System.out.println("Not an HTTP Request");
        }

        System.out.println("--------------HeaderLoggerValve----------------------");
    }


    public String getInfo() {
        return null;
    }
}