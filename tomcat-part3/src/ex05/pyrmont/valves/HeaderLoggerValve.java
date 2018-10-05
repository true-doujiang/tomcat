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
 * 一个阀表示一个具体的任务 
 * 
 * 获取客户端的请求头信息
 */
public class HeaderLoggerValve implements Valve, Contained {

	protected Container container;

	public void invoke(Request request, Response response,
			ValveContext valveContext) throws IOException, ServletException {

		//先调用下一个阀   在执行本阀中任务, 所以虽然是先执行了但却是后输出
		// Pass this request on to the next valve in our pipeline
		valveContext.invokeNext(request, response);

		System.out.println(Thread.currentThread().getName() + " Header Logger Valve");
		ServletRequest sreq = request.getRequest();
		
		if (sreq instanceof HttpServletRequest) {
			HttpServletRequest hreq = (HttpServletRequest) sreq;
			Enumeration headerNames = hreq.getHeaderNames();
			
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement().toString();
				String headerValue = hreq.getHeader(headerName);
				System.out.println(Thread.currentThread().getName() + " " + headerName + ":" + headerValue);
			}
		} else {
			System.out.println(Thread.currentThread().getName() + "Not an HTTP Request");
		}

		System.out.println(Thread.currentThread().getName() + " ------------------------------------");
	}

	public String getInfo() {
		return null;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}
}