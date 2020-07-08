package Interchat;

import java.io.Closeable;

/*释放资源
 * */
public class Utils {
    public static void close(Closeable... targets) {//...表示可传多个参数
        for (Closeable target : targets) {
            try {
                if (null != target)
                    target.close();
            } catch (Exception e) {

            }
        }
    }
}
