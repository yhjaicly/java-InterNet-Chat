package Interchat;

import java.io.*;
import java.net.Socket;

/*使用多线程封装发送端
 * */
public class Send implements Runnable {
    private BufferedReader console;
    private DataOutputStream dos;
    private Socket client;
    private boolean isRunning = true;
    private String name;

    public Send(Socket client, String name) {

        this.client = client;
        this.name = name;
        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            dos = new DataOutputStream(client.getOutputStream());
            //发送用户名
            send(name);
        } catch (IOException e) {
            release();
        }

    }

    //释放资源
    private void release() {
        isRunning = false;
        Utils.close(dos, client);
    }

    //发送消息
    private void send(String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (isRunning) {
            String msg = "";
            try {
                msg = console.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!msg.equals("")) {
                send(msg);
            }

        }
    }
}
