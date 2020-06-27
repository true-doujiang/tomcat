package ex03.pyrmont.connector.http;

/**
 * this class copies methods from org.apache.catalina.connector.HttpRequestBase
 * and org.apache.catalina.connector.http.HttpRequestImpl.
 * The HttpRequestImpl class employs a pool of HttpHeader objects for performance
 * These two classes will be explained in Chapter 4.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.util.Enumerator;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;

import ex03.pyrmont.connector.RequestStream;

/**
 * 第二章则是实现的javax.servlet.ServletRequest接口
 */
public class HttpRequest implements HttpServletRequest {

    //HttpProcess.parseHeader中设置的
    private String contentType;
    //
    private int contentLength;

    private InetAddress inetAddress;

    //HttpProcessor中创建HttpRequest对象是设置的 SocketInputStream
    private InputStream input;

    private String method;
    private String protocol;
    // 参数： ?name=yhh&age=18
    private String queryString;
    private String requestURI;

    private String serverName;
    private int serverPort;
    private Socket socket;
    private boolean requestedSessionCookie;

    //浏览器禁用cookie时，用我存放url中会话标识符
    private String requestedSessionId;
    //url中带会话标识符是设置true
    private boolean requestedSessionURL;

    protected HashMap attributes = new HashMap();
    //
    protected String authorization = null;
    protected String contextPath = "";

    /**
     * Have the parameters for this request been parsed yet?
     * 标记请求参数是否解析过
     */
    protected boolean parsed = false;
    protected String pathInfo = null;

    //继承自HashMap其中其中有一个locked 布尔变量，只有当为false才可以添加、删除、修改，否则抛出异常
    //读取可以发生任何时候
    protected ParameterMap parameters = null;
    //
    protected ArrayList cookies = new ArrayList();
    //{host=[localhost:8080], connection=[Close], content-type=[text/plain]}
    protected HashMap headers = new HashMap();

    /**
     * An empty collection to use for returning empty Enumerations.  Do not
     * add any elements to this collection!
     */
    protected static ArrayList empty = new ArrayList();
    /**
     * The set of SimpleDateFormat formats to use in getDateHeader().
     */
    protected SimpleDateFormat formats[] = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)
    };


    /**
     * The reader that has been returned by <code>getReader</code>, if any.
     */
    protected BufferedReader reader = null;

    /**
     * The ServletInputStream that has been returned by
     * <code>getInputStream()</code>, if any.
     */
    protected ServletInputStream stream = null;

    //
    public HttpRequest(InputStream input) {
        this.input = input;
    }

    /**
     * Parse the parameters of this request, if it has not already occurred.
     * If parameters are present in both the query string and the request
     * content, they are merged.
     * 
     * 当servlet程序员在servlet中调用了
     *      getParameter()
     *      getParameterMap()
     *      getAttributeNames
     *      getParameterValues
     *
     * 才会触发该方法解析请求参数.
     *
     * 若为GET请求，所有参数都会在查询字符串中
     * 若为POST请求，则请求体中也可能包含参数
     *
     */
    protected void parseParameters() {
        if (parsed) {
            return;
        }
        ParameterMap results = parameters;
        if (results == null) {
            //ParameterMap中的 去掉了，不然一直报错
            // StringManager sm =  StringManager.getManager("org.apache.catalina.util");
            results = new ParameterMap();
        }
        //打开ParameterMap锁
        results.setLocked(false);
        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }

        // 若为GET请求方式    解析url中的参数
        // Parse any parameters specified in the query string
        String queryString = getQueryString();
        try {
        	//
            RequestUtil.parseParameters(results, queryString, encoding);
        } catch (UnsupportedEncodingException e) {
            ;
        }

        // Parse any parameters specified in the input stream
        String contentType = getContentType();
        if (contentType == null) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(';');
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }

        // 若为POST 请求方式  请求体中的参数
        if ("POST".equals(getMethod()) && (getContentLength() > 0) 
        		&& "application/x-www-form-urlencoded".equals(contentType)) {
        	
            try {
                int max = getContentLength();
                int len = 0;
                byte buf[] = new byte[getContentLength()];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                is.close();
                if (len < max) {
                    throw new RuntimeException("Content length mismatch");
                }
                RequestUtil.parseParameters(results, buf, encoding);
            } catch (UnsupportedEncodingException ue) {
                ;
            } catch (IOException e) {
                throw new RuntimeException("Content read fail");
            }
        }

        // Store the final results
        results.setLocked(true);
        parsed = true;
        parameters = results;
    }

    /**
     * parseHeaders()调用
     * 
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (values == null) {
                values = new ArrayList();
                headers.put(name, values);
            }
            values.add(value);
        }
    }

    /**
     * parseHeaders()调用
     *  
     * @param cookie
     */
    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }

    /**
     * Create and return a ServletInputStream to read the content
     * associated with this Request.  The default implementation creates an
     * instance of RequestStream associated with this request, but this can
     * be overridden if necessary.
     *
     * @exception IOException if an input/output error occurs
     */
    public ServletInputStream createInputStream() throws IOException {
        return (new RequestStream(this));
    }

    public InputStream getStream() {
        return input;
    }

    /**
     * parseHeaders()调用
     * @param length
     */
    public void setContentLength(int length) {
        this.contentLength = length;
    }

    /**
     * parseHeaders()调用 
     * @param type
     */
    public void setContentType(String type) {
        this.contentType = type;
    }

    public void setInet(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setContextPath(String path) {
        if (path == null)
            this.contextPath = "";
        else
            this.contextPath = path;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPathInfo(String path) {
        this.pathInfo = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    /**
     * Set the name of the server (virtual host) to process this request.
     *
     * @param name The server name
     */
    public void setServerName(String name) {
        this.serverName = name;
    }

    /**
     * Set the port number of the server to process this request.
     *
     * @param port The server port
     */
    public void setServerPort(int port) {
        this.serverPort = port;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Set a flag indicating whether or not the requested session ID for this
     * request came in through a cookie.  This is normally called by the
     * HTTP Connector, when it parses the request headers.
     *
     * @param flag The new flag
     */
    public void setRequestedSessionCookie(boolean flag) {
        this.requestedSessionCookie = flag;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public void setRequestedSessionURL(boolean flag) {
        requestedSessionURL = flag;
    }

    /* implementation of the HttpServletRequest*/
    public Object getAttribute(String name) {
        synchronized (attributes) {
            return (attributes.get(name));
        }
    }

    public Enumeration getAttributeNames() {
        synchronized (attributes) {
            return (new Enumerator(attributes.keySet()));
        }
    }

    public String getAuthType() {
        return null;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContextPath() {
        return contextPath;
    }

    public Cookie[] getCookies() {
        synchronized (cookies) {
            if (cookies.size() < 1) {
                return (null);
            }
            Cookie results[] = new Cookie[cookies.size()];
            return ((Cookie[]) cookies.toArray(results));
        }
    }

    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return (-1L);
        }

        // Work around a bug in SimpleDateFormat in pre-JDK1.2b4
        // (Bug Parade bug #4106807)
        value += " ";

        // Attempt to convert the date header in a variety of formats
        for (int i = 0; i < formats.length; i++) {
            try {
                Date date = formats[i].parse(value);
                return (date.getTime());
            } catch (ParseException e) {
                ;
            }
        }
        throw new IllegalArgumentException(value);
    }

    public String getHeader(String name) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (values != null)
                return ((String) values.get(0));
            else
                return null;
        }
    }

    public Enumeration getHeaderNames() {
        synchronized (headers) {
            return (new Enumerator(headers.keySet()));
        }
    }

    public Enumeration getHeaders(String name) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (values != null) {
                return (new Enumerator(values));
            } else {
                return (new Enumerator(empty));
            }
        }
    }

    public ServletInputStream getInputStream() throws IOException {
        if (reader != null)
            throw new IllegalStateException("getInputStream has been called");

        if (stream == null) {
            stream = createInputStream();
        }
        return (stream);
    }

    public int getIntHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return (-1);
        } else {
            return (Integer.parseInt(value));
        }
    }

    public Locale getLocale() {
        return null;
    }

    public Enumeration getLocales() {
        return null;
    }

    public String getMethod() {
        return method;
    }

    public String getParameter(String name) {
        parseParameters();
        String values[] = (String[]) parameters.get(name);
        if (values != null)
            return (values[0]);
        else
            return (null);
    }

    public Map getParameterMap() {
        parseParameters();
        return (this.parameters);
    }

    public Enumeration getParameterNames() {
        parseParameters();
        return (new Enumerator(parameters.keySet()));
    }

    public String[] getParameterValues(String name) {
        parseParameters();
        String values[] = (String[]) parameters.get(name);
        if (values != null)
            return (values);
        else
            return null;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return null;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getQueryString() {
        return queryString;
    }

    public BufferedReader getReader() throws IOException {
        if (stream != null)
            throw new IllegalStateException("getInputStream has been called.");
        if (reader == null) {
            String encoding = getCharacterEncoding();
            if (encoding == null) {
                encoding = "ISO-8859-1";
            }
            InputStreamReader isr = new InputStreamReader(createInputStream(), encoding);
            reader = new BufferedReader(isr);
        }
        return (reader);
    }

    public String getRealPath(String path) {
        return null;
    }

    public String getRemoteAddr() {
        return null;
    }

    public String getRemoteHost() {
        return null;
    }

    public String getRemoteUser() {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public String getScheme() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public int getServerPort() {
        return 0;
    }

    public String getRequestedSessionId() {
        return null;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        return null;
    }

    public HttpSession getSession() {
        return null;
    }

    public HttpSession getSession(boolean create) {
        return null;
    }

    public String getServletPath() {
        return null;
    }

    public Principal getUserPrincipal() {
        return null;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    public boolean isRequestedSessionIdValid() {
        return false;
    }

    public boolean isSecure() {
        return false;
    }

    public boolean isUserInRole(String role) {
        return false;
    }

    public void removeAttribute(String attribute) {
    }

    public void setAttribute(String key, Object value) {
    }

    /**
     * Set the authorization credentials sent with this request.
     *
     * @param authorization The new authorization credentials
     */
    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
    }
}
