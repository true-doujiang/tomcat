package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.StringManager;
import ex03.pyrmont.ServletProcessor;
import ex03.pyrmont.StaticResourceProcessor;

/* this class used to be called HttpServer */
/**
 * 作者: 尤欢欢
 * 日期： 2018年8月12日 下午1:06:13
 * 描述： 每个请求都有一个 HttpProcessor  负责创建Request和Response对象
 */
public class HttpProcessor {

    /**
     * The HttpConnector with which this processor is associated.
     * 本应用并没有用到它
     */
    private HttpConnector connector = null;

    // 请求行信息    GET /myapp/ModernServlet?username=aa HTTP/1.1
    private HttpRequestLine requestLine = new HttpRequestLine();
    private HttpRequest request;
    private HttpResponse response;

    //并没有用到
//    protected String method = null;
//    protected String queryString = null;

    protected StringManager sm = StringManager.getManager("ex03.pyrmont.connector.http");


    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }
    
    /**
     * process()方法接收来自传入的HTTP请求的套接字。 并要完成4个动作
     * 1.创建一个HttpRequest对象
     * 2.创建一个HttpResponse对象
     * 3.解析HTTP请求的第一行内容和请求头信息，填充HttpRequest对象
     * 4.将HTTPRequest对象和HTTPResponse对象传递给servletProcessor或者StaticResourceProcessor的process()方法。
     *
     * @param socket
     * @param count  我自己新添加的方便计数
     */
    public void process(Socket socket, int count) {
        SocketInputStream input = null;
        OutputStream output = null;
        try {
            input = new SocketInputStream(socket.getInputStream(), 2048);
            output = socket.getOutputStream();

            //创建Request和Response对象
            request = new HttpRequest(input);

            response = new HttpResponse(output);
            response.setRequest(request);
            //设置响应头   是放进去了，但是HttpServletResponse版本太低没有提供getHeader()方法，所以用户代码中拿不到这俩个header
            response.setHeader("Server", "Pyrmont Servlet Container");
            response.setHeader("Content-Type", "text/html");
            
            /**
             * （解析第一行内容 和请求头信息）  GET /myapp/ModernServlet?username=aa HTTP/1.1
             * 
             * 解析HTTP请求中的请求行和请求头信息，并将其填充到HttpRequest对象成员变量中
             * 但是 并不会解析请求体和查询字符串中的参数。 这个任务交给各个HttpRequest对象自己完成
             */
            parseRequest(input, output);
            //解析请求头信息 cookie,并将其填充到HttpRequest对象成员变量中
            parseHeaders(input);

            // 核心模块处理
            if (request.getRequestURI().startsWith("/servlet/")) {
            	System.out.println(Thread.currentThread().getName() + " 处理Servlet请求  request = " + request + "   response = " + response);

                ServletProcessor processor = new ServletProcessor();
                processor.process(request, response);
            } else {
                System.out.println(Thread.currentThread().getName() + " 处理静态资源请求  request = " + request + "   response = " + response);

                StaticResourceProcessor processor = new StaticResourceProcessor();
                processor.process(request, response);
            }
            
            System.out.println(Thread.currentThread().getName() + " 关闭 socket" + count + " " + socket);
            System.out.println();
            socket.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param input
     * @param output
     * @throws IOException
     * @throws ServletException
     */
    private void parseRequest(SocketInputStream input, OutputStream output)
            						throws IOException, ServletException {

        //调用readRequestLine()方法，使用SocketInputStream对象中的信息填充到HttpRequestLine实例
        // Parse the incoming request line
        input.readRequestLine(requestLine);
        
        String method = new String(requestLine.method, 0, requestLine.methodEnd);
        String uri = null;
        String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

        // Validate the incoming request line
        if (method.length() < 1) {
            throw new ServletException("Missing HTTP request method");
        } else if (requestLine.uriEnd < 1) {
            throw new ServletException("Missing HTTP request URI");
        }
        
        /*
         *  Parse any query parameters out of the request URI
         *   解析请求参数、URI 设置到 HttpRequest 中
         */
        int question = requestLine.indexOf("?");
        if (question >= 0) {
            request.setQueryString(new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
            uri = new String(requestLine.uri, 0, question);
        } else {
            request.setQueryString(null);
            uri = new String(requestLine.uri, 0, requestLine.uriEnd);
        }


        /*
         *  Checking for an absolute URI (with the HTTP protocol)
         *  检测URI是否为绝对路径
         */
        if (!uri.startsWith("/")) {
            int pos = uri.indexOf("://");
            // Parsing out protocol and host name
            if (pos != -1) {
                pos = uri.indexOf('/', pos + 3);
                if (pos == -1) {
                    uri = "";
                } else {
                    uri = uri.substring(pos);
                }
            }
        }

        /*
         *  Parse any requested session ID out of the request URI
         *  检查是否包含会话标识。 
         *  若包含 需要获取jsessionid的值，并调用HttpRequest类的setRequestedSessionId() 填充HttpRequest实例
         */
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if (semicolon >= 0) {
        	// 说明cookie被禁用 session放在url后面了
            String rest = uri.substring(semicolon + match.length());
            int semicolon2 = rest.indexOf(';');
            if (semicolon2 >= 0) {
                request.setRequestedSessionId(rest.substring(0, semicolon2));
                rest = rest.substring(semicolon2);
            } else {
                //设置会话标识符
                request.setRequestedSessionId(rest);
                rest = "";
            }
            //包含会话标识符设置true
            request.setRequestedSessionURL(true);
            //请求uri
            uri = uri.substring(0, semicolon) + rest;
        } else {
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }

        /*
         *  Normalize URI (using String operations at the moment)
         *  修正URI 例如，出现\的地方会被替换为 /
         */
        String normalizedUri = normalize(uri);

        /*
         *  Set the corresponding request properties
         *  设置HttpRequest对象的一些属性
         */
        request.setMethod(method);
        request.setProtocol(protocol);
        
        if (normalizedUri != null) {
            request.setRequestURI(normalizedUri);
        } else {
            request.setRequestURI(uri);
        }

        // 抛异常
        if (normalizedUri == null) {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
    }

    /**
     * 解析请求头信息 cookie,并将其填充到HttpRequest对象成员变量中
     * @param input
     * @throws IOException
     * @throws ServletException
     */
    private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
        //循环一次读一个请求头
        while (true) {

            HttpHeader header = new HttpHeader();
            // Read the next header
            input.readHeader(header);
            
            //可以通过检查HttpHeader实例的nameEnd、valueEnd字段来判断是否已经从输入流中读取了所有请求头
            //都读完了时nameEnd、valueEnd字段为0
            if (header.nameEnd == 0) {
                if (header.valueEnd == 0) {
                    return;
                } else {
                    throw new ServletException (sm.getString("httpProcessor.parseHeaders.colon"));
                }
            }

            //获取请求头的名字 和 值
            String name = new String(header.name, 0, header.nameEnd);
            String value = new String(header.value, 0, header.valueEnd);
            System.out.println(Thread.currentThread().getName() + " httpProcessor.parseHeaders --> " + name + "  = " + value);
            
            //存入request中
            request.addHeader(name, value);
            
            // do something for some headers, ignore others.
            if (name.equals("cookie")) {
                //解析Cookie 
                Cookie cookies[] = RequestUtil.parseCookieHeader(value);
                
                for (int i = 0; i < cookies.length; i++) {
                    if (cookies[i].getName().equals("jsessionid")) {
                        // Override anything requested in the URL 
                        if (!request.isRequestedSessionIdFromCookie()) {
                            // Accept only the first session id cookie
                            request.setRequestedSessionId(cookies[i].getValue());
                            request.setRequestedSessionCookie(true);
                            request.setRequestedSessionURL(false);
                        }
                    }
                    //
                    request.addCookie(cookies[i]);
                }
            } else if (name.equals("content-length")) {
                int n = -1;
                try {
                    n = Integer.parseInt(value);
                } catch (Exception e) {
                    throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
                request.setContentLength(n);
            } else if (name.equals("content-type")) {
                request.setContentType(value);
            }
        } //end while
    }


    /**
     * 
     * @param path
     * @return
     */
    protected String normalize(String path) {
        if (path == null) {
            return null;
        }
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.indexOf("%25") >= 0)
                || (normalized.indexOf("%2F") >= 0)
                || (normalized.indexOf("%2E") >= 0)
                || (normalized.indexOf("%5C") >= 0)
                || (normalized.indexOf("%2f") >= 0)
                || (normalized.indexOf("%2e") >= 0)
                || (normalized.indexOf("%5c") >= 0)) {
            return null;
        }

        if (normalized.equals("/.")) {
            return "/";
        }

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0) {
                break;
            }
            if (index == 0) {
                return (null);  // Trying to go outside our context
            }
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0) {
            return (null);
        }

        // Return the normalized path that we have completed
        return (normalized);

    }

}
