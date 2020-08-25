package tv.fengmang.xeniadialog.download;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

//不用，
@Deprecated
public class DownloadUtils {
    String TAG = "DownloadUtils";

    /**
     * 没有断点续传功能的get请求下载
     *
     * @param downUrl             下载网络地址
     * @param saveFilePathAndName 保存路径
     */
    public void download(final String downUrl, final File saveFilePathAndName) {
        Request request = new Request.Builder()
                .url(downUrl)
                .get()
                .build();
        Call mCall = new OkHttpClient().newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                long mTotalLength = responseBody.contentLength();//下载文件的总长度
                Log.d(TAG, "mTotalLength:" + mTotalLength);
                InputStream inp = responseBody.byteStream();
                FileOutputStream fileOutputStream = new FileOutputStream(saveFilePathAndName);
                try {
                    byte[] bytes = new byte[2048];
                    int len = 0;
                    while ((len = inp.read(bytes)) != -1) {
                        mTotalLength += len;
                        fileOutputStream.write(bytes, 0, len);
                    }
                    Log.d(TAG, "mTotalLength:" + mTotalLength);
                } catch (Exception e) {
                    Log.e(TAG, "Get下载异常");
                } finally {
                    fileOutputStream.close();
                    inp.close();
                    Log.e(TAG, "流关闭");
                }
            }
        });
    }
}
