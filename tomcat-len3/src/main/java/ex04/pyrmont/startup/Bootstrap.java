/* explains Tomcat's default container */
package ex04.pyrmont.startup;

import org.apache.catalina.connector.http.HttpConnector;

import ex04.pyrmont.core.SimpleContainer;

/**
 * 疑问1 socket 什么时候关闭的? 在 HttpProcessor.process()
 * 疑问2 浏览器已经显示servlet的输出了   为什么 HttpProcessor.process() 还没有执行完呢   而且执行了很久 1m
 */
public final class Bootstrap {

	//本应用不能处理静态请求
	public static void main(String[] args) {
		/**
		 * Tomcat连接器必须满足以下要求: 
		 * 1.等待HTTP请求 
		 * 2.创建request、response对象 
		 * 3.调用Container接口的invoke() 将request、response对象传递给servlet容器
		 */
		HttpConnector connector = new HttpConnector();
		//创建Servlet容器
		SimpleContainer container = new SimpleContainer();
		//连接器与Servlet容器是一对一的关系
		connector.setContainer(container);
		
		try {
			/**
			 * 由于HttpConnector类实现了Lifecycle接口，
			 * 因此当创建一个HttpConnector实例后，就应该调用器initialize() 、start()
			 * 而且在组件的整个生命周期内只应该调用一次。
			 */
			connector.initialize();
			connector.start();

			// make the application wait until we press any key.
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}






