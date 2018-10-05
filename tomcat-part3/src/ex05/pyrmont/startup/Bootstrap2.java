package ex05.pyrmont.startup;

import ex05.pyrmont.core.SimpleContext;
import ex05.pyrmont.core.SimpleContextMapper;
import ex05.pyrmont.core.SimpleLoader;
import ex05.pyrmont.core.SimpleWrapper;
import ex05.pyrmont.valves.ClientIPLoggerValve;
import ex05.pyrmont.valves.HeaderLoggerValve;
import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap2 {

	public static void main(String[] args) {
		//TOMCAT默认连接器
		HttpConnector connector = new HttpConnector();
		
		//创建2个Wrapper容器
		Wrapper wrapper1 = new SimpleWrapper();
		wrapper1.setName("Primitive");
		wrapper1.setServletClass("PrimitiveServlet");

		Wrapper wrapper2 = new SimpleWrapper();
		wrapper2.setName("Modern");
		wrapper2.setServletClass("ModernServlet");
		
		//创建Context容器,并添加2个子容器
		Context context = new SimpleContext();
		context.addChild(wrapper1);
		context.addChild(wrapper2);

		//Context容器添加2个阀
		Valve valve1 = new HeaderLoggerValve();
		Valve valve2 = new ClientIPLoggerValve();
		((Pipeline) context).addValve(valve1);
		((Pipeline) context).addValve(valve2);
		
		//Context容器添加映射器组件
		Mapper mapper = new SimpleContextMapper();
		mapper.setProtocol("http");
		context.addMapper(mapper);
		
		//Context容器添加载入器
		Loader loader = new SimpleLoader();
		context.setLoader(loader);
		
		// context.addServletMapping(pattern, name);
		context.addServletMapping("/Primitive", "Primitive");
		context.addServletMapping("/Modern", "Modern");
		
		connector.setContainer(context);
		
		try {
			connector.initialize();
			connector.start();

			// make the application wait until we press a key.
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}