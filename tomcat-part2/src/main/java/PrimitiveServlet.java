import ex02.pyrmont.Request;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet implements Servlet {

    public void init(ServletConfig config) throws ServletException {
        System.out.println("init");
    }

    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {

        /**
         * 应用程序1 的缺点
         * 了解这个Servlet容器内部工作原理的Servlet程序员可以将
         * (ex02.pyrmont.Request)request
         * (ex02.pyrmont.Response)response
         * 然后就可以调用到request.parse()   response.sendStaticResource()
         * 太不安全了
         */

        System.out.println("from service ------start------ ");
        System.out.println("this=" + this + " request=" + request + " response=" + response);

        PrintWriter out = response.getWriter();
        System.out.println(Thread.currentThread().getName() + " PrimitiveServlet PrintWriter = " + out);
        Request req = (Request) request;
        String uri = req.getUri();
        System.out.println(Thread.currentThread().getName() + " 我居然调用到了容器里Request的私有方法 uri=" + uri);

        out.println("Hello. Roses are red.");
        out.print("Violets are blue.");
        System.out.println("from service --------end------ ");
    }

    public void destroy() {
        System.out.println("destroy");
    }

    public String getServletInfo() {
        return "ServletIinfo";
    }

    public ServletConfig getServletConfig() {
        return null;
    }

}
