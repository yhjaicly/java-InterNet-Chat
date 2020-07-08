package Interchat;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/*使用多线程封装接收端
 * */
public class Receive implements Runnable {
    private DataInputStream dis;//获取输入的信息
    private Socket client;
    private boolean isRunning = true;

    public Receive(Socket client) {
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            release();
        }
    }

    //释放资源
    private void release() {
        isRunning = false;
        Utils.close(dis, client);
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String msg = dis.readUTF();
                if (!msg.equals("")) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
