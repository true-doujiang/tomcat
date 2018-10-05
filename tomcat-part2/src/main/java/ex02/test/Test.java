package ex02.test;

import java.io.*;

public class Test {

    public static void main(String[] args) throws FileNotFoundException {
        f();
    }

    public static void f () throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(new File("E:\\spring.log"));

        // autoflush is true, println() will flush,
        // but print() will not.
        PrintWriter pw = new PrintWriter(fos, true);  //前提true   以下来个成立
        pw.println("aaa"); //println() 不会主动写入磁盘
        pw.print("bbb"); //print() 不会主动写入磁盘

        //pw.close();
    }

    public static void classLoader () {
        //ClassLoader

    }


}
