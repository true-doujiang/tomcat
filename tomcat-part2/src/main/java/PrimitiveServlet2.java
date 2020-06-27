import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet2 extends GenericServlet {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) {
        System.out.println("PrimitiveServlet2 -- init");
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Modern Servlet</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h2>Headers</h2");

        out.println("<br><h2>Method</h2");

        out.println("<br><h2>Parameters</h2");

        out.println("<br><h2>Query String</h2");

        out.println("<br><h2>Request URI</h2");

        out.println("</body>");
        out.println("</html>");
    }


}