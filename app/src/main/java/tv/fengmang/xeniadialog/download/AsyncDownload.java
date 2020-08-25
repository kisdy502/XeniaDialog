package tv.fengmang.xeniadialog.download;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import tv.fengmang.xeniadialog.download.DownloadHelper.ProgressListener;

/**
 * 异步下载干活的类，只需要一个OkHttp3不依赖任何第三方，不需要业务bean简单容易移植
 * 支持断点续传
 */
class AsyncDownload {

    public final static int ERROR_HTTP = 0; //请求出错
    public final static int ERROR_IO = 1;   //response 存文件出错
    private byte[] buff = new byte[8 * 1024];

    private final static String TAG = "AsyncDownload";
    private long start = 0L;

    private Call mCall;

    /**
     * 回调监听列表，可能会发生变化所以，遍历必须使用迭代器来遍历
     */
    private List<DownloadHelper.ProgressListener> progressListenerList;
    private String downloadUrl;


    AsyncDownload(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    void refreshProgressListener(List<ProgressListener> listenerList) {
        this.progressListenerList = listenerList;
    }

    void download(File saveDir, String saveFileName) {
        final File file = new File(saveDir, saveFileName);        //设置路径
        start = 0;
        if (file.exists()) {
            start = file.length();
        }
        progressListenerList = DownloadHelper.getListenerList(downloadUrl);

        if (!validUrl()) {
            return;
        }

        Request.Builder builder = new Request.Builder();
        Log.i(TAG, "bytes=" + start + "-");
        builder.addHeader("RANGE", "bytes=" + start + "-");
        Request request = builder.url(downloadUrl).build();
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new ProgressInterceptor())
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS).build();
        mCall = client.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure:", e);
                Iterator iterator = progressListenerList.iterator();
                while (iterator.hasNext()) {
                    ((ProgressListener) iterator.next()).onFailed(downloadUrl, ERROR_HTTP, e.getMessage());
                }
                DownloadHelper.removeListenerAndTask(downloadUrl);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "response:" + response.code());
                if (response.isSuccessful()) {
                    if (response.body().contentLength() == 0) {
                        Log.d(TAG, "finish download:");
                    } else {
                        saveFile(response, file);
                    }
                } else {
                    Iterator iterator = progressListenerList.iterator();
                    if (response.code() == 416) {
                        //416处理
                        while (iterator.hasNext()) {
                            ((ProgressListener) iterator.next()).onUpdate(downloadUrl, start, file.length(), start == file.length());
                        }
                    } else {
                        while (iterator.hasNext()) {
                            ((ProgressListener) iterator.next()).onFailed(downloadUrl, ERROR_HTTP, "错误的状态码:" + response.code());
                        }
                    }
                    DownloadHelper.removeListenerAndTask(downloadUrl);
                }
            }
        });
    }

    private boolean validUrl() {
        HttpUrl parsed = HttpUrl.parse(downloadUrl);
        if (parsed == null) {
            Iterator iterator = progressListenerList.iterator();
            while (iterator.hasNext()) {
                ((ProgressListener) iterator.next()).onFailed(downloadUrl, ERROR_HTTP, "下载链接无效!");
            }
            DownloadHelper.removeListenerAndTask(downloadUrl);
            return false;
        }
        return true;
    }


    private boolean saveFile(Response response, File saveFile) {
        Log.d(TAG, "saveFile:" + saveFile.getAbsolutePath());
        InputStream is = null;
        RandomAccessFile randomAccessFile = null;
        BufferedInputStream bis = null;
        try {
            int len = 0;
            is = response.body().byteStream();
            bis = new BufferedInputStream(is);
            // 随机访问文件，可以指定断点续传的起始位置
            randomAccessFile = new RandomAccessFile(saveFile, "rwd");
            randomAccessFile.seek(start);
            while ((len = bis.read(buff)) != -1) {
                randomAccessFile.write(buff, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.w(TAG, "save file exception" + e.getMessage());
            if (e instanceof SocketException) {
                Log.i(TAG, "pause download or disconnected!");
            } else {
                Iterator iterator = progressListenerList.iterator();
                while (iterator.hasNext()) {
                    ((ProgressListener) iterator.next()).onFailed(downloadUrl, ERROR_IO, e.getMessage());
                }
                DownloadHelper.removeListenerAndTask(downloadUrl);
            }
            return false;
        } finally {
            close(bis);
            close(is);
            close(randomAccessFile);
        }
    }

    public void cancel() {
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
        }
    }

    private class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody) {
            this.responseBody = responseBody;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {

            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    Iterator iterator = progressListenerList.iterator();
                    while (iterator.hasNext()) {
                        ((ProgressListener) iterator.next()).onUpdate(downloadUrl, totalBytesRead + start, responseBody
                                .contentLength() + start, bytesRead == -1);
                    }
                    if (bytesRead == -1) {
                        DownloadHelper.removeListenerAndTask(downloadUrl);
                    }
                    return bytesRead;
                }
            };
        }
    }


    private class ProgressInterceptor implements Interceptor {

        private ProgressInterceptor() {
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body()))
                    .build();
        }
    }


    private static void close(Closeable... closeable) {
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
