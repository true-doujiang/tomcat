package ex01.test;

import java.io.*;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException {

//        System.out.println("|||\r2"); //r  return   回车
//        System.out.println("|\n 2");  //n  newline  换行
//        System.out.println("|\r\n 2");

        f();
    }

    public static void f() throws IOException {
        FileInputStream fis = new FileInputStream(new File("E:\\spring.log"));
        int len = fis.available();
        int len2 = fis.available();
        System.out.println(len + " " + len2);

        byte[] bytes = new byte[len];
        int b = fis.read(bytes, 0, len);
        System.out.println(b);
        System.out.println(Arrays.toString(bytes));

        FileOutputStream fos = new FileOutputStream(new File("E:\\spring2.log"));
        fos.write(bytes, 0, 5);
        FileOutputStream fos3 = new FileOutputStream(new File("E:\\spring3.log"));
        fos3.write(bytes, 0, b);
    }
}
