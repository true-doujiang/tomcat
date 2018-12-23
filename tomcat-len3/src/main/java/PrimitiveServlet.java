import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class PrimitiveServlet implements Servlet {

    public void init(ServletConfig config) throws ServletException {
        System.out.println("init");
    }

    /**
     * 这个类没有放到webapp下   为什么会执行呢????????????
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {


        System.out.println(Thread.currentThread().getName() + " from service ------start111------ ");
        System.out.println(Thread.currentThread().getName() + " this=" + this + " request=" + request + " response=" + response);

        PrintWriter out = response.getWriter();
        System.out.println(Thread.currentThread().getName() + " PrimitiveServlet PrintWriter = " + out);

//        out.println("Hello. Roses are red.");
//        out.print("Violets are blue.");



        out.println("<html>");
        out.println("<head>");
        out.println("<title>PrimitiveServlet Servlet</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<br><h2>Parameters</h2");
        Enumeration parameters = request.getParameterNames();
        while (parameters.hasMoreElements()) {
            String parameter = (String) parameters.nextElement();
            out.println("<br>" + parameter + " : " + request.getParameter(parameter));
        }


        out.println("</body>");
        out.println("</html>");



        System.out.println(Thread.currentThread().getName() + " from service --------end111------ ");
    }

    public void destroy() {
        System.out.println("destroy");
    }

    public String getServletInfo() {
        return null;
    }

    public ServletConfig getServletConfig() {
        return null;
    }

}
