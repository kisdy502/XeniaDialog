package tv.fengmang.xeniadialog.log;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;


/**
 * Created by Administrator on 2018/8/21.
 */
class ECSWriteTask {

    private File mRootLogDir;
    private File mLogFile;
    private long max_size = 10485760L;   //file max length:10MB
    private RandomAccessFile raf;

    public ECSWriteTask(File rootDir) {
        this(rootDir, 10485760L);
    }

    public ECSWriteTask(String filePath) {
        this(new File(filePath), 10485760L);
    }

    public ECSWriteTask(String filePath, long max_size) {
        this(new File(filePath), max_size);
    }

    public ECSWriteTask(File rootDir, long max_size) {
        this.mRootLogDir = rootDir;
        this.max_size = max_size;
        pool.execute(new WriteThread());
    }

    private LinkedBlockingQueue<LogBean> printQueue = new LinkedBlockingQueue<LogBean>(2048);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private ExecutorService pool = Executors.newCachedThreadPool(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    });


    public void addTask(int level, String tag, String msg) {
        Date date = new Date();
        LogBean bean = new LogBean();
        bean.timeStr = sdf.format(date);
        bean.level = level;
        bean.tag = tag;
        bean.msg = msg;
        printQueue.add(bean);

    }

    /**
     * 太耗时了，而且无用处
     *
     * @param level
     * @param tag
     * @param msg
     */
    @Deprecated
    public synchronized void addTask2(int level, String tag, String msg) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            for (int i = 0, len = stackTraceElements.length; i < len; ++i) {
                if (i == 5) {
                    StackTraceElement t = stackTraceElements[i];
                    Date date = new Date();
                    LogBean bean = new LogBean();
                    bean.timeStr = sdf.format(date);
                    bean.level = level;
                    bean.tag = tag;
                    bean.msg = msg;
                    printQueue.add(bean);
                }
            }
        }
    }


    private void write(String sb) {
        checkFile();
        try {
            raf = new RandomAccessFile(mLogFile, "rw");
            raf.seek(mLogFile.length());
            raf.write(sb.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(raf);
        }
    }

    private void checkFile() {
        long size = mLogFile.length();
        if (size > this.max_size) {
            createCpyFile(mLogFile, true);
        }
    }

    public void createCpyFile(File oldFile, boolean deleteOld) {
        File newFile = new File(oldFile.getAbsolutePath() + ".bak");
        if (newFile.exists()) {
            newFile.delete();
        }
        copyFile(oldFile, newFile);
        if (deleteOld) {
            oldFile.delete();
        }
    }

    public void copyFile(File oldFile, File newFile) {
        Log.e("ecs", "日志满了，复制到临时文件");
        if (oldFile != null && newFile != null) {
            InputStream is = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            String line = "";

            try {
                is = new FileInputStream(oldFile);
                br = new BufferedReader(new InputStreamReader(is));
                bw = new BufferedWriter(new FileWriter(newFile));

                while ((line = br.readLine()) != null) {
                    bw.write(line);
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(is);
                IOUtils.close(br);
                IOUtils.close(bw);
            }
        }
    }

    private void initLogFile() throws IOException {
        mLogFile = new File(mRootLogDir, ECS.LOG_FILE_NAME);
        if (!mLogFile.exists()) {
            boolean fileOK = mLogFile.createNewFile();
            Log.d("ECS", "ESC Log File:" + fileOK);
        }
        Log.d("ECS", "ESC Log File:" + mLogFile.getAbsolutePath());


    }

    private class WriteThread implements Runnable {

        private WriteThread() {
            Log.d("WriteThread", "write log at work thread");
        }

        public void run() {
            try {
                initLogFile();
                while (true) {
                    LogBean logBean = printQueue.take();
                    write(logBean.toString());
                    Thread.yield();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
