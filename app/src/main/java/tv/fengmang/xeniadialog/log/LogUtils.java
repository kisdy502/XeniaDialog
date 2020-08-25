package tv.fengmang.xeniadialog.log;

/**
 * LogUtils
 *
 * @author vio_wang
 * @date 2018-12-25
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import tv.fengmang.xeniadialog.crash.FileHelper;

/**
 * Created by elena on 2017/9/4.
 */

public class LogUtils {
    private static final String TAG = "LogUtils";
    private static LogUtils INSTANCE = null;
    private static File logDir;
    private LogDumper mLogDumper = null;
    private int mPId;

    /**
     * 初始化目录
     */
    public void init(Context context) {
        logDir = FileHelper.getExternalLogDir(context);
        if (logDir == null) {
            logDir = FileHelper.getInnerLogDir(context);
        }
        Log.d(TAG, "PATH_LOGCAT:" + logDir.getAbsolutePath());
    }

    public static LogUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogUtils(context);
        }
        return INSTANCE;
    }

    private LogUtils(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), logDir);
        }
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {
        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, File dir) {
            mPID = pid;
            try {
                out = new FileOutputStream(new File(dir, getFileName() ));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e *:w *:i *:d | grep \"(" + mPID + ")\" | grep -vE \"^..MenuRecyclerView|^..FileListActivity\"";
            Log.d(TAG, "cmds:" + cmds);

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()),
                        4096);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }
            }
        }
    }

    public String getFileName() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH");
        String date = format.format(new Date(System.currentTimeMillis()));
        Log.d(TAG, "date:" + date);
        return date.concat("log.txt");// 2012年10月03日 23:41:31
    }
}
