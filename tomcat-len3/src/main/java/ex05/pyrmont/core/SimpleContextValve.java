package ex05.pyrmont.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;

/**
 * @author youhh
 * @desc
 */
public class SimpleContextValve implements Valve, Contained {

    protected Container container;


    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     *
     */
    public void invoke(Request req, Response resp, ValveContext valveContext)
            throws IOException, ServletException {

        // Validate the request and response object types
        if (!(req.getRequest() instanceof HttpServletRequest) || !(resp.getResponse() instanceof HttpServletResponse)) {
            // NOTE - Not much else we can do generically
            return;
        }

        // Disallow any direct access to resources under WEB-INF or META-INF
        HttpServletRequest hreq = (HttpServletRequest) req.getRequest();
        String contextPath = hreq.getContextPath();

        String requestURI = ((HttpRequest) req).getDecodedRequestURI();
        String relativeURI = requestURI.substring(contextPath.length()).toUpperCase();

        // 获取Context容器
        Context context = (Context) getContainer();

        // Select the Wrapper to be used for this Request
        Wrapper wrapper = null;
        try {
            /**
             * 这里的逻辑 挺重要的
             */
            wrapper = (Wrapper) context.map(req, true);
        } catch (IllegalArgumentException e) {
            badRequest(requestURI, (HttpServletResponse) resp.getResponse());
            return;
        }

        if (wrapper == null) {
            notFound(requestURI, (HttpServletResponse) resp.getResponse());
            return;
        }

        // Ask this Wrapper to process this Request
        resp.setContext(context);

        wrapper.invoke(req, resp);
    }

    public String getInfo() {
        return null;
    }


    /**
     *
     */
    private void badRequest(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }
    }

    /**
     *
     */
    private void notFound(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
        } catch (IllegalStateException e) {
            ;
        } catch (IOException e) {
            ;
        }
    }

}