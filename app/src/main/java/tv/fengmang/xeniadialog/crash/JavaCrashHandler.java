package tv.fengmang.xeniadialog.crash;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JavaCrashHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = "JavaCrashHandler";
    private Thread.UncaughtExceptionHandler defaultHandler = null;

    private Date startTime;
    private Date crashTime;
    private String appId;
    private String appVersion;
    private File crashLogDir;

    public JavaCrashHandler(String appId, String appVersion, File logDir) {
        startTime = new Date();
        this.appId = appId;
        this.appVersion = appVersion;
        this.crashLogDir = logDir;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        try {
            Log.d(TAG, "JavaCrashHandler init......");
            Thread.setDefaultUncaughtExceptionHandler(this);
        } catch (Exception e) {
            Log.e(TAG, "JavaCrashHandler setDefaultUncaughtExceptionHandler failed", e);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        crashTime = new Date();
        if (defaultHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
        } else {
            Log.e(TAG, "defaultUncaughtExceptionHandler is null");
        }

        try {
            handleException(thread, throwable);
        } catch (Exception e) {
            Log.e(TAG, "JavaCrashHandler handleException failed", e);
        }

        if (defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
    }

    private void handleException(Thread thread, Throwable throwable) {
        String stacktrace = CrashHelper.getStackTrace(startTime, crashTime, thread,
                throwable, appId, appVersion);

        Log.d(TAG, "stacktrace:\n" + stacktrace);

//        String networkInfo = CrashHelper.getNetworkInfo();
//
//        Log.d(TAG, "networkInfo:\n" + networkInfo);

        String memoryInfo = CrashHelper.getMemoryInfo();

        Log.d(TAG, "memoryInfo:\n" + memoryInfo);

        String logcat = CrashHelper.getLogcat(200, 50, 50);

        Log.d(TAG, "logcat:\n" + logcat);

        File crashFile = initCrashFile();
        if (crashFile != null) {
            RandomAccessFile raf = null;
            try {
                raf = new RandomAccessFile(crashFile, "rws");
                if (stacktrace != null) {
                    raf.write(stacktrace.getBytes("UTF-8"));
                }

//                if (networkInfo != null) {
//                    raf.write(networkInfo.getBytes("UTF-8"));
//                }

                if (memoryInfo != null) {
                    raf.write(memoryInfo.getBytes("UTF-8"));
                }

                if (logcat != null) {
                    raf.write(logcat.getBytes("UTF-8"));
                }
            } catch (Exception e) {
                Log.e(TAG, "JavaCrashHandler write log file failed", e);
            } finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    private File initCrashFile() {
        DateFormat timeFormatter = new SimpleDateFormat("yyyy_MM_dd'T'HH", Locale.US);
        String fileName = timeFormatter.format(crashTime) + "_crash.log";
        File file = new File(crashLogDir, fileName);
        if (!file.exists()) {
            try {
                boolean ok = file.createNewFile();
                if (ok) {
                    Log.d(TAG, "`create:" + file.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void writeData(File file, String content) {

    }
}
