package ex05.pyrmont.startup;

import ex05.pyrmont.core.SimpleLoader;
import ex05.pyrmont.core.SimpleWrapper;
import ex05.pyrmont.valves.ClientIPLoggerValve;
import ex05.pyrmont.valves.HeaderLoggerValve;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

public final class Bootstrap1 {


    public static void main(String[] args) {

        /**
         * call by using http://localhost:8080/ModernServlet,
         *    but could be invoked by any name
         */

        HttpConnector connector = new HttpConnector();

        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("ModernServlet");

        // 类加载器
        Loader loader = new SimpleLoader();
        // Wrapper容器设置类加载器
        wrapper.setLoader(loader);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();
        ((Pipeline) wrapper).addValve(valve1);
        ((Pipeline) wrapper).addValve(valve2);

        // Wrapper容器设置到连接器中
        connector.setContainer(wrapper);

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