package Interchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/*在线聊天服务端
 * */
public class Chat {
    private static CopyOnWriteArrayList<Channel> all = new CopyOnWriteArrayList<>();//容器用于存放聊天的线程

    public static void main(String[] args) throws IOException {
        //指定端口，使用ServerSocket创建服务器
        ServerSocket server = new ServerSocket(8888);
        while (true) {
            //阻塞式连接
            Socket client = server.accept();

            Channel c = new Channel(client);
            all.add(c);//管理所有的资源
            new Thread(c).start();

        }
    }
//收发消息类
    static class Channel implements Runnable {
        private DataInputStream dis;//输入流
        private DataOutputStream dos;//输出流
        private Socket client;
        private boolean isRunning;
        private String name;//用户的名字

        public Channel(Socket client) {
            this.client = client;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                isRunning = true;
                name = receive();//获取名称
                //欢迎你的到来
                send("欢迎你的到来");
                sendOthers(name + "来到了聊天室", true);
            } catch (IOException e) {
                release();
            }
        }

        //接收消息
        private String receive() {
            String msg = "";//避免空指针
            try {
                msg = dis.readUTF();//读取接收到的消息
            } catch (IOException e) {
                release();//出现问题，关闭所有对象
            }
            return msg;
        }

        //发送消息
        private void send(String msg) {
            try {
                dos.writeUTF(msg);//发送消息
                dos.flush();//刷新
            } catch (IOException e) {
                release();
            }
        }

        //发送群聊
        //私聊：约定数据格式：@xxx:msg
        private void sendOthers(String msg, boolean isSys) {
            boolean isPrivate = msg.startsWith("@");
            if (isPrivate) {//私聊
                int idx = msg.indexOf(":");//:第一次出现的位置，获取名字长度
                //获取目标和数据
                String targetName = msg.substring(1, idx);//获取名字
                msg = msg.substring(idx + 1);//获取信息
                for (Channel other : all) {
                    if (other.name.equals(targetName)) {
                        other.send(name + "对你私聊说:" + msg);
                        break;
                    }
                }
            } else {
                for (Channel other : all) {
                    if (other == this) {//如果是自己的话跳过自己
                        continue;
                    }
                    if (!isSys) {//判断是否为系统消息
                        other.send(this.name + "：" + msg);
                    } else {
                        other.send(msg);
                    }
                }
            }
        }

        //释放资源
        private void release() {
            Utils.close(dis, dos, client);
            all.remove(this);
            sendOthers("爷走了", true);
        }

        @Override
        public void run() {
            while (isRunning) {//线程一直运行
                String msg = receive();
                if (!msg.equals("")) {
                    sendOthers(msg, false);//群发
                }
            }
        }
    }
}