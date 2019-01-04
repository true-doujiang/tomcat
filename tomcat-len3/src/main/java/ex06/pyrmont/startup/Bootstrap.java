package ex06.pyrmont.startup;

import ex06.pyrmont.core.SimpleContext;
import ex06.pyrmont.core.SimpleContextLifecycleListener;
import ex06.pyrmont.core.SimpleContextMapper;
import ex06.pyrmont.core.SimpleLoader;
import ex06.pyrmont.core.SimpleWrapper;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Mapper;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

/**
 * @author youhh
 *
 * @desc 生命周期  启动类
 */
public final class Bootstrap {
    
    
    public static void main(String[] args) {
        Connector connector = new HttpConnector();
    
        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
    
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");

        Context context = new SimpleContext();
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        // 设置映射器
        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        // 给SimpleContext容器设置监听器  并没有给其它组件设置
        LifecycleListener listener = new SimpleContextLifecycleListener();
        ((Lifecycle) context).addLifecycleListener(listener);
        
        // 设置类加载器
        Loader loader = new SimpleLoader();
        context.setLoader(loader);

        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");
        
        connector.setContainer(context);
        
        try {
            connector.initialize();
            ((Lifecycle) connector).start();
            ((Lifecycle) context).start();

            // make the application wait until we press a key.
            System.in.read();
            ((Lifecycle) context).stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}