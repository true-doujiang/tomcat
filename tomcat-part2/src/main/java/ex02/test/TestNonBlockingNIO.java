package ex02.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class TestNonBlockingNIO {

    private static int countMsg =  1;
    private static final int blockSize = 4096;
    private static ByteBuffer sendbuffer = ByteBuffer.allocate(blockSize);
    private static ByteBuffer receivebuffer = ByteBuffer.allocate(blockSize);

    public static void main(String[] args) throws IOException {
        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        //2. 切换非阻塞模式
        sChannel.configureBlocking(false);
        //3. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4. 发送数据给服务端
        Scanner scan = new Scanner(System.in);

        String input = null;
        while (!"close".equals(input = scan.nextLine())) {
            System.out.println("输入: " + input);
            buf.put(input.getBytes());
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        Selector selector = Selector.open();
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey> it;
        SelectionKey selectionKey;
        SocketChannel client;
        String receiveText;
        String sendText;
        int count = 0;
        selectionKeys = selector.selectedKeys();
        it = selectionKeys.iterator();
        while (it.hasNext()) {
            selectionKey = it.next();

            if (selectionKey.isConnectable()) {
                System.out.println("client connect");
                client = (SocketChannel) selectionKey.channel();

                //判断是否真的连接成功
                if (client.isConnectionPending()) {
                    client.finishConnect();
                    System.out.println("客户端完成连接操作 connected");

                    sendbuffer.clear();
                    sendbuffer.put("get /index.html".getBytes());
                    sendbuffer.flip();
                    client.write(sendbuffer);
                }
                client.register(selector, SelectionKey.OP_READ);

            } else if (selectionKey.isReadable()) {
                client = (SocketChannel) selectionKey.channel();
                receivebuffer.clear();
                count = client.read(receivebuffer);
                if (count > 0) {
                    receiveText = new String(receivebuffer.array(), 0, count);
                    System.out.println("客户端接收到服务端的数据:<-------" + receiveText);
                    client.register(selector, SelectionKey.OP_WRITE);
                }

            } else if (selectionKey.isWritable()) {
                sendbuffer.clear();
                client = (SocketChannel) selectionKey.channel();
                sendText = "今天天气怎么样" + countMsg++;
                sendbuffer.put(sendText.getBytes());
                sendbuffer.flip();
                client.write(sendbuffer);
                System.out.println("客户端发送数据到服务端: ------->" + sendText);
                client.register(selector, SelectionKey.OP_READ);
            }


        }

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //5. 关闭通道
        sChannel.close();
        System.out.println("over");
    }

}
