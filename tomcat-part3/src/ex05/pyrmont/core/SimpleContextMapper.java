package ex05.pyrmont.core;

import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.Container;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Mapper;
import org.apache.catalina.Request;
import org.apache.catalina.Wrapper;

/**
 * 映射器组件：
 * 		帮助servlet容器选择一个子容器来处理某个指定的请求。
 * servlet容器可以使用多个映射器来支持不同的协议。（HTTP、HTTPS）   
 *
 */
public class SimpleContextMapper implements Mapper {

	/**
	 * The Container with which this Mapper is associated.
	 */
	private SimpleContext context = null;

	public Container getContainer() {
		return (context);
	}
	
	/**
	 * 加入的必须是SimpleContext容器
	 */
	public void setContainer(Container container) {
		if (!(container instanceof SimpleContext))
			throw new IllegalArgumentException("Illegal type of container");
		context = (SimpleContext) container;
	}

	/**
	 * Return the child Container that should be used to process this Request,
	 * based upon its characteristics. If no such child Container can be
	 * identified, return <code>null</code> instead.
	 * 
	 * @param request
	 *            Request being processed
	 * @param update
	 *            Update the Request to reflect the mapping selection?
	 * 
	 * @exception IllegalArgumentException
	 *                if the relative portion of the path cannot be URL decoded
	 * 
	 * map()会返回一个子容器（也就是Wrapper实例）来处理请求。
	 * map()需要俩个参数，一个request对象和一个布尔变量。 在本应用中忽略了第二个参数
	 * 
	 * map()方法会从request对象中解析出请求的上下文路径，并调用Context是实例的findServletMapping()
	 * 来获取一个与该路径相关联的名称。 如果找到了这个名称，则调用Context实例的findChild()来获取一个Wrapper实例。
	 */
	public Container map(Request request, boolean update) {
		// Identify the context-relative URI to be mapped
		String contextPath = ((HttpServletRequest) request.getRequest()).getContextPath();
		String requestURI = ((HttpRequest) request).getDecodedRequestURI();
		String relativeURI = requestURI.substring(contextPath.length());
		
		// Apply the standard request URI mapping rules from the specification
		Wrapper wrapper = null;
		String servletPath = relativeURI;
		String pathInfo = null;
		
		String name = context.findServletMapping(relativeURI);
		if (name != null)
			wrapper = (Wrapper) context.findChild(name);
		return (wrapper);
	}
	
	//我自己加的
	public String protocol;
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}