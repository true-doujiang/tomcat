package ex05.pyrmont.core;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.catalina.Cluster;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.InstanceListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Logger;
import org.apache.catalina.Manager;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Realm;
import org.apache.catalina.Request;
import org.apache.catalina.Response;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;

/**
 *  Wrapper容器 : 
 *  Wrapper级的容器表示一个独立的servlet定义。
 *  Wrapper接口的实现类要负责管理其基础servlet类的servlet生命周期， 即调用servlet的init()、service()、 destroy() 等.
 *  Wrapper接口比较重要的俩个方法allocate()  load()
 *  
 *  
 *  1、容器可以包含一些支持的组件， 如载入器、记录器、管理器、领域、和资源。
 *    并且提供了get、set方法将这些组件与容器相关联
 *  2、一个servlet容器可以有一条管道, 当调用了容器的invoke() 后，容器会将处理工作交由管道完成。
 */
public class SimpleWrapper implements Wrapper, Pipeline {
	
	//Wrapper级的容器表示一个独立的servlet定义。
	// the servlet instance
	private Servlet instance = null;
	//
	private String servletClass;
	private String name;

	//该容器的父容器	
	protected Container parent = null;
	
	//该容器的servlet载入器
	private Loader loader;

	//容器的管道    包含容器要执行的任务，每个阀是一个具体的任务
	private SimplePipeline pipeline = new SimplePipeline(this);
	

	public SimpleWrapper() {
		//一个容器可以一个管道， 而管道一定有一个基础阀  ，所以基础阀就在这里设置了
		pipeline.setBasic(new SimpleWrapperValve());
	}

	//org.apache.catalina.core.ContainerBase  中也是这么实现的
	public void invoke(Request request, Response response) 
					throws IOException, ServletException {
		pipeline.invoke(request, response);
	}

	public synchronized void addValve(Valve valve) {
		pipeline.addValve(valve);
	}

	/**
	 * 加载一个Servlet
	 */
	public Servlet allocate() throws ServletException {
		// Load and initialize our instance if necessary
		if (instance == null) {
			try {
				instance = loadServlet();
			} catch (ServletException e) {
				throw e;
			} catch (Throwable e) {
				throw new ServletException("Cannot allocate a servlet instance", e);
			}
		}
		return instance;
	}

	public void load() throws ServletException {
		instance = loadServlet();
	}
	
	private Servlet loadServlet() throws ServletException {
		if (instance != null)
			return instance;

		Servlet servlet = null;
		String actualClass = servletClass;
		if (actualClass == null) {
			throw new ServletException("servlet class has not been specified");
		}

		Loader loader = getLoader();
		// Acquire an instance of the class loader to be used
		if (loader == null) {
			throw new ServletException("No loader.");
		}
		ClassLoader classLoader = loader.getClassLoader();

		// Load the specified servlet class from the appropriate class loader
		Class classClass = null;
		try {
			if (classLoader != null) {
				classClass = classLoader.loadClass(actualClass);
			}
		} catch (ClassNotFoundException e) {
			throw new ServletException("Servlet class not found");
		}
		// Instantiate and initialize an instance of the servlet class itself
		try {
			//实例化servlet
			servlet = (Servlet) classClass.newInstance();
			System.out.println(Thread.currentThread().getName() + " SimpleWrapper 容器 实例化servlet = " + servlet);  
		} catch (Throwable e) {
			throw new ServletException("Failed to instantiate servlet");
		}

		// Call the initialization method of this servlet
		try {
			//初始化servlet
			servlet.init(null);
		} catch (Throwable f) {
			throw new ServletException("Failed initialize servlet.");
		}
		return servlet;
	}

	public String getInfo() {
		return null;
	}

	public Loader getLoader() {
		if (loader != null)
			return (loader);
		if (parent != null)
			return (parent.getLoader());
		return (null);
	}

	public void setLoader(Loader loader) {
		this.loader = loader;
	}

	public Logger getLogger() {
		return null;
	}

	public void setLogger(Logger logger) {
	}

	public Manager getManager() {
		return null;
	}

	public void setManager(Manager manager) {
	}

	public Cluster getCluster() {
		return null;
	}

	public void setCluster(Cluster cluster) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Container getParent() {
		return parent;
	}

	public void setParent(Container container) {
		parent = container;
	}

	public ClassLoader getParentClassLoader() {
		return null;
	}

	public void setParentClassLoader(ClassLoader parent) {
	}

	public Realm getRealm() {
		return null;
	}

	public void setRealm(Realm realm) {
	}

	public DirContext getResources() {
		return null;
	}

	public void setResources(DirContext resources) {
	}

	public long getAvailable() {
		return 0;
	}

	public void setAvailable(long available) {
	}

	public String getJspFile() {
		return null;
	}

	public void setJspFile(String jspFile) {
	}

	public int getLoadOnStartup() {
		return 0;
	}

	public void setLoadOnStartup(int value) {
	}

	public String getRunAs() {
		return null;
	}

	public void setRunAs(String runAs) {
	}

	public String getServletClass() {
		return null;
	}

	public void setServletClass(String servletClass) {
		this.servletClass = servletClass;
	}

	public void addChild(Container child) {
	}

	public void addContainerListener(ContainerListener listener) {
	}

	public void addMapper(Mapper mapper) {
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
	}

	public Container findChild(String name) {
		return null;
	}

	public Container[] findChildren() {
		return null;
	}

	public ContainerListener[] findContainerListeners() {
		return null;
	}

	public void addInitParameter(String name, String value) {
	}

	public void addInstanceListener(InstanceListener listener) {
	}

	public void addSecurityReference(String name, String link) {
	}

	public void deallocate(Servlet servlet) throws ServletException {
	}

	public String findInitParameter(String name) {
		return null;
	}

	public String[] findInitParameters() {
		return null;
	}

	public String findSecurityReference(String name) {
		return null;
	}

	public String[] findSecurityReferences() {
		return null;
	}

	public Mapper findMapper(String protocol) {
		return null;
	}

	public Mapper[] findMappers() {
		return null;
	}

	public boolean isUnavailable() {
		return false;
	}


	public Container map(Request request, boolean update) {
		return null;
	}

	public void removeChild(Container child) {
	}

	public void removeContainerListener(ContainerListener listener) {
	}

	public void removeMapper(Mapper mapper) {
	}

	public void removeInitParameter(String name) {
	}

	public void removeInstanceListener(InstanceListener listener) {
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
	}

	public void removeSecurityReference(String name) {
	}

	public void unavailable(UnavailableException unavailable) {
	}

	public void unload() throws ServletException {
	}

	// method implementations of Pipeline
	public Valve getBasic() {
		return pipeline.getBasic();
	}

	public void setBasic(Valve valve) {
		pipeline.setBasic(valve);
	}

	public Valve[] getValves() {
		return pipeline.getValves();
	}

	public void removeValve(Valve valve) {
		pipeline.removeValve(valve);
	}

}