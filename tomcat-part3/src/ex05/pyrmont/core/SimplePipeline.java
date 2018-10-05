package ex05.pyrmont.core;

import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.catalina.Contained;
import org.apache.catalina.Container;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

/**
 * 管道
 * Pipeline接口中invoke() 与 Container接口中的invoke()方法完全相同
 */
public class SimplePipeline implements Pipeline {

	// The basic Valve (if any) associated with this Pipeline.
	protected Valve basic = null;
	
	//把该管道 与所属的容器相关联
	// The Container with which this Pipeline is associated.
	protected Container container = null;
	
	// the array of Valves
	protected Valve valves[] = new Valve[0];

	public SimplePipeline(Container container) {
		setContainer(container);
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}

	public Valve getBasic() {
		return basic;
	}

	public void setBasic(Valve valve) {
		this.basic = valve;
		((Contained) valve).setContainer(container);
	}

	public void addValve(Valve valve) {
		if (valve instanceof Contained) {
			//如果该阀实现了Contained接口 ，这也是Contained接口的作用
			((Contained) valve).setContainer(this.container);
		}

		synchronized (valves) {
			Valve results[] = new Valve[valves.length + 1];
			System.arraycopy(valves, 0, results, 0, valves.length);
			results[valves.length] = valve;
			valves = results;
		}
	}

	public Valve[] getValves() {
		return valves;
	}
	
	/**
	 * 在创建了ValveContext实例后， 管道会调用ValveContext实例的invokeNext() 开始遍历执行管道中的阀
	 */
	public void invoke(Request request, Response response) throws IOException, ServletException {
		// Invoke the first Valve in this pipeline for this request
		(new SimplePipelineValveContext()).invokeNext(request, response);
	}

	public void removeValve(Valve valve) {
	}

	// this class is copied from org.apache.catalina.core.StandardPipeline
	// class's
	// StandardPipelineValveContext inner class.
	protected class SimplePipelineValveContext implements ValveContext {

		protected int stage = 0;

		public void invokeNext(Request request, Response response) throws IOException, ServletException {
			int subscript = stage;
			stage = stage + 1;
			
			// Invoke the requested Valve for the current request thread
			if (subscript < valves.length) {
				valves[subscript].invoke(request, response, this);
			} else if ((subscript == valves.length) && (basic != null)) {
				basic.invoke(request, response, this);
			} else {
				throw new ServletException("No valve");
			}
		}
		
		public String getInfo() {
			return null;
		}
	} // end of inner class

}