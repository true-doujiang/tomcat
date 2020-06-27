package ex02.pyrmont;

import java.io.IOException;


/**
 * 
 * @author -小野猪-
 * 静态资源请求处理
 *
 */
public class StaticResourceProcessor {

    public void process(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}