package ex05.pyrmont.core;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Mapper;
import org.apache.catalina.Request;
import org.apache.catalina.Wrapper;


/**
 * @author youhh
 *
 * @desc 映射器 implements Mapper   需要与对应的容器相关联
 */
public class SimpleContextMapper implements Mapper {

    /**
     * The Container with which this Mapper is associated.
     */
    private SimpleContext context = null;


    /**
     * Return the child Container that should be used to process this Request,
     * based upon its characteristics.  If no such child Container can be
     * identified, return <code>null</code> instead.
     *
     * @param request Request being processed
     * @param update  Update the Request to reflect the mapping selection?
     * @throws IllegalArgumentException if the relative portion of the
     *                                  path cannot be URL decoded
     *
     *  返回要处理某个特定请求的子容器实例
     *  本应用忽略了第二个参数
     */
    public Container map(Request request, boolean update) {

        // Identify the context-relative URI to be mapped
        String contextPath =((HttpServletRequest) request.getRequest()).getContextPath();
        String requestURI = ((HttpRequest) request).getDecodedRequestURI();

        String relativeURI = requestURI.substring(contextPath.length());
        // Apply the standard request URI mapping rules from the specification

        Wrapper wrapper = null;
        String servletPath = relativeURI;
        String pathInfo = null;

        /**
         * 获取一个与该路径相关联的名称
         * 找到了就获取相应的子容器
         */
        String name = context.findServletMapping(relativeURI);
        if (name != null) {
            wrapper = (Wrapper) context.findChild(name);
        }

        return wrapper;
    }

    /**
     * 返回与该映射器相关联的Servlet容器实例
     * @return
     */
    public Container getContainer() {
        return context;
    }

    /**
     * 需要与对应的容器相关联
     */
    public void setContainer(Container container) {
        if (!(container instanceof SimpleContext)) {
            throw new IllegalArgumentException("Illegal type of container");
        }
        context = (SimpleContext) container;
    }

    public String getProtocol() {
        return null;
    }

    /**
     *
     */
    public void setProtocol(String protocol) {
    }


}