package ex02.pyrmont;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author -小野猪-
 * Servlet请求处理
 *
 */
public class ServletProcessor1 {

    public void process(Request request, Response response) {

        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;

        try {
            // create a URLClassLoader
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);

            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            System.out.println("repository: " + repository);

            urls[0] = new URL(null, repository, streamHandler);

            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            //System.err.println(e.toString());
            e.printStackTrace();
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;

        try {
            servlet = (Servlet) myClass.newInstance();
            System.out.println("servlet=" + servlet +" request=" + request +" response=" + response);

            /**
             * 执行servlet
             * request向上转型ServletRequest是为了不让在Servlet里调用到request中的parse()
             * response向上转型ServletResponse是为了不让在Servlet里调用到request中的sendStaticResource()
             *
             * 应用程序2用了包装设计模式解决了这个问题
             * 直接调用service() 就不用init()
             */
            servlet.service((ServletRequest) request, (ServletResponse) response);

        } catch (Exception e) {
            System.err.println(e.toString());
        } catch (Throwable e) {
            System.err.println(e.toString());
        }

    }
}