package Interchat;

import java.io.*;
import java.net.Socket;

/*在线聊天客户端
 * */
public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("请输入用户名：");
        //BufferedReader是字符输入流中读取文本，缓冲各个字符，从而提供字符、数组和行的高效读取！速度要比Scanner快
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));//用户输入姓名可用Scanner
        String name = br.readLine();//获取用户输入的姓名

        //建立连接：使用Socket创建客户端+服务的地址和端口
        Socket client = new Socket("localhost", 8888);
        //客户端发送消息
        new Thread(new Send(client, name)).start();
        //客户端接收消息
        new Thread(new Receive(client)).start();

    }
}
