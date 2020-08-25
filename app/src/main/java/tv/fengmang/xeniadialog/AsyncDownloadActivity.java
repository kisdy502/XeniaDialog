package tv.fengmang.xeniadialog;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import tv.fengmang.xeniadialog.download.DownloadHelper;
import tv.fengmang.xeniadialog.utils.MD5Utils;

public class AsyncDownloadActivity extends AppCompatActivity {

    private Button btnDownload1;
    private Button btnDownload2;
    private Button btnDownload3;
    private EditText edtIp;
    private static String QQ_URL = "http://apk.beemarket.tv/OMS/20190520/13110/Beemarket_1.31.10_default.apk";
    //    private static String QQ_URL = "httpgxxaaa://apk.beemarket.tv/OMS/20190520/13110/Beemarket_1.31.11_default.apk"; //TEST ERROR URL2
    //   private static String QQ_URL = "";   //TEST ERROR URL2
    private static String FILE_NAME = "Beemarket_1.31.10_default.apk";
    private static String TAG = "AsyncDownload";
    private static String TAG1 = "AsyncDownload1";
    private static String TAG2 = "AsyncDownload2";
    private static String TAG3 = "AsyncDownload3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        btnDownload1 = findViewById(R.id.btn_download01);
        btnDownload2 = findViewById(R.id.btn_download02);
        btnDownload3 = findViewById(R.id.btn_download03);
        edtIp = findViewById(R.id.edt_ip);

        btnDownload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download1();
            }
        });

        btnDownload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadHelper.stop(QQ_URL);
            }
        });

        btnDownload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 3; i++) {
                    if (i == 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                download1();
                            }
                        }.start();
                    } else if (i == 1) {
                        new Thread() {
                            @Override
                            public void run() {
                                download2();
                            }
                        }.start();
                    } else if (i == 2) {
                        new Thread() {
                            @Override
                            public void run() {
                                download3();
                            }
                        }.start();
                    }
                }

            }
        });

        findViewById(R.id.btn_clear_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), FILE_NAME);
                file.delete();
            }
        });
    }

    private void download1() {
        //回调都是在子线程，如果刷新UI的话,需要再切回主线程
        final File dir = getExternalFilesDir("apk");
        DownloadHelper.start(QQ_URL, dir, FILE_NAME, new DownloadHelper.ProgressListener() {
            @Override
            public void onFailed(String downloadUrl, int result, String desc) {
                Log.e(TAG1, "onFailed:" + downloadUrl);
                Log.e(TAG1, "result:" + result);
                Log.e(TAG1, "desc:" + desc);
            }

            @Override
            public void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done) {
                boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                if (done) {
                    Log.i(TAG1, "url:" + downloadUrl + "," + bytesRead + "," + contentLength);
                    File file = new File(dir, FILE_NAME);
                    String md5 = MD5Utils.getFileMD5(file);
                    Log.i(TAG1, "MD5:" + md5 + "," + isMainThread);
                }
            }
        });
    }

    private void download2() {
        //回调都是在子线程，如果刷新UI的话,需要再切回主线程
        DownloadHelper.start(QQ_URL, getCacheDir(), FILE_NAME, new DownloadHelper.ProgressListener() {
            @Override
            public void onFailed(String downloadUrl, int result, String desc) {
                Log.e(TAG2, "onFailed:" + downloadUrl);
                Log.e(TAG2, "result:" + result);
                Log.e(TAG2, "desc:" + desc);
            }

            @Override
            public void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done) {
                boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                Log.e(TAG2, "url:" + downloadUrl + "," + bytesRead + "," + contentLength + "," + done);
                if (done) {
                    File file = new File(getCacheDir(), FILE_NAME);
                    String md5 = MD5Utils.getFileMD5(file);
                    Log.i(TAG2, "MD5:" + md5 + "," + isMainThread);
                }
            }
        });
    }

    private void download3() {
        //回调都是在子线程，如果刷新UI的话,需要再切回主线程
        DownloadHelper.start(QQ_URL, getCacheDir(), FILE_NAME, new DownloadHelper.ProgressListener() {
            @Override
            public void onFailed(String downloadUrl, int result, String desc) {
                Log.e(TAG3, "onFailed:" + downloadUrl);
                Log.e(TAG3, "result:" + result);
                Log.e(TAG3, "desc:" + desc);
            }

            @Override
            public void onUpdate(String downloadUrl, long bytesRead, long contentLength, boolean done) {
                boolean isMainThread = Looper.getMainLooper() == Looper.myLooper();
                Log.e(TAG3, "url:" + downloadUrl + "," + bytesRead + "," + contentLength + "," + done);
                if (done) {
                    File file = new File(getCacheDir(), FILE_NAME);
                    String md5 = MD5Utils.getFileMD5(file);
                    Log.i(TAG3, "MD5:" + md5 + "," + isMainThread);
                }
            }
        });
    }


}
