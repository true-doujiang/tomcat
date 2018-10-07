package ex03.pyrmont.startup;

import ex03.pyrmont.connector.http.HttpConnector;

/**
 * 应用程序 ：ex03.pyrmont
 * 连接器: ex03.pyrmont.connector  ex03.pyrmont.connector.http
 * 
 * 本章应用包含3个模块
 * 启动模块： 负责启动应用程序
 * 连接器模块：
 * 		  连接器类 及其 支持类 HttpConnector、HttpProcessor
 * 		  表示HTTP请求的类    HttpRequest 及其支持类
 *        表示HTTP响应的类    HttpResponse 及其支持类
 *        外观类  HttpResponseFacade HttpRequestFacade
 *        常量类 Constants
 * 核心模块：
 * 		 ServletProcess、StaticResourceProcess
 */
public final class Bootstrap {

	//java compiler 1.4  本工程用的1.4的编译器
	
	
	// servlet/PrimitiveServlet
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
}





















