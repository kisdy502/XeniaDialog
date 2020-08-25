package tv.fengmang.xeniadialog.log;

import java.io.Closeable;

/**
 * Created by Administrator on 2018/9/4.
 */

public class IOUtils {
    public static void close(Closeable... closeable) {
        for (Closeable c : closeable) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
