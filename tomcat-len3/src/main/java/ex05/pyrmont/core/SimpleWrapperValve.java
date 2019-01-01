package ex05.pyrmont.core;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;

/**
 * @author youhh
 *
 * @desc SimpleWrapper类的基础阀
 */
public class SimpleWrapperValve implements Valve, Contained {

    /**
     * 该基础阀的宿主类
     */
    protected Container container;


    public Container getContainer() {
        return container;
    }

    /**
     *　SimpleWrapper()构造器中开始设置的
     *
     *  debug查看调用执行栈就知道了
     */
    public void setContainer(Container container) {
        this.container = container;
    }

    /**
     * 1.基础阀的invoke方法 不需要调用传递给它的ValveContext实例的invokeNext方法
     * 2.invoke方法 会调用SimpleWrapper类allocate()来获取Wrapper实例表示的Servlet的实例
     */
    public void invoke(Request request, Response response, ValveContext valveContext)
            throws IOException, ServletException {
        //基础阀的invoke方法 不需要调用传递给它的ValveContext实例的invokeNext方法

        //
        SimpleWrapper wrapper = (SimpleWrapper) getContainer();

        ServletRequest sreq = request.getRequest();
        ServletResponse sresp = response.getResponse();

        Servlet servlet = null;
        HttpServletRequest hreq = null;
        if (sreq instanceof HttpServletRequest) {
            hreq = (HttpServletRequest) sreq;
        }
        HttpServletResponse hres = null;
        if (sresp instanceof HttpServletResponse) {
            hres = (HttpServletResponse) sresp;
        }

        // Allocate a servlet instance to process this request
        try {

            // invoke方法 会调用SimpleWrapper类allocate()来获取Wrapper实例表示的Servlet的实例
            servlet = wrapper.allocate();

            // 注意是Wrapper实例的基础阀调用了Servlet实例的sevice(),而不是Wrapper实例本身调用的
            if (hres != null && hreq != null) {
                servlet.service(hreq, hres);
            } else {
                servlet.service(sreq, sresp);
            }

        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() {
        return null;
    }

}