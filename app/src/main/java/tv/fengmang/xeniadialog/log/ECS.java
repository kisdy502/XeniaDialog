package tv.fengmang.xeniadialog.log;


import java.io.File;

/**
 * 将日志写入到文件中
 * Created by Administrator on 2018/8/21.
 */
public class ECS {

    public static final String LOG_FILE_NAME = "ECS.txt";

    ECSWriteTask ecsWriteTask;

    public ECS(File rootDir) {
        ecsWriteTask = new ECSWriteTask(rootDir);
    }

    public ECS(File rootDir, long maxLength) {
        ecsWriteTask = new ECSWriteTask(rootDir, maxLength);
    }

    public void v(String tag, String msg) {
        ecsWriteTask.addTask(ELog.LEVEL_V, tag, msg);
    }

    public void d(String tag, String msg) {
        ecsWriteTask.addTask(ELog.LEVEL_D, tag, msg);
    }


    public void i(String tag, String msg) {
        ecsWriteTask.addTask(ELog.LEVEL_I, tag, msg);
    }

    public void w(String tag, String msg) {
        ecsWriteTask.addTask(ELog.LEVEL_W, tag, msg);
    }


    public void e(String tag, String msg) {
        ecsWriteTask.addTask(ELog.LEVEL_E, tag, msg);
    }
}
