package ex05.pyrmont.valves;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletException;

import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;

/**
 * 一个阀表示一个具体的任务 
 * 
 * 获取客户端IP地址
 */
public class ClientIPLoggerValve implements Valve, Contained {

	protected Container container;

	public void invoke(Request request, Response response,
			ValveContext valveContext) throws IOException, ServletException {
		
		//先调用下一个阀   在执行本阀中任务, 所以虽然是先执行了但却是后输出
		// Pass this request on to the next valve in our pipeline
		valveContext.invokeNext(request, response);
		
		System.out.println(Thread.currentThread().getName() + " Client IP Logger Valve");
		ServletRequest sreq = request.getRequest();
		System.out.println(Thread.currentThread().getName() + " " + sreq.getRemoteAddr());
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