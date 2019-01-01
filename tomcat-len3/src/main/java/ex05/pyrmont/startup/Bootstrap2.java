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
        //
        HttpConnector connector = new HttpConnector();

        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");

        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        // 创建 Context 容器
        Context context = new SimpleContext();

        // 1. 添加子容器
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();

        // 2. 添加阀
        ((Pipeline) context).addValve(valve1);
        ((Pipeline) context).addValve(valve2);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");

//        Mapper mapper2 = new SimpleContextMapper();
//        mapper2.setProtocol("https");

        // 3. 添加映射器
        context.addMapper(mapper);
//        context.addMapper(mapper2);

        Loader loader = new SimpleLoader();
        // 4. 添加类加载器
        context.setLoader(loader);

        // context.addServletMapping(pattern, name);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        //
        connector.setContainer(context);

        try {
            connector.initialize();
            connector.start();
            // make the application wait until we press a key.
            int n = System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}