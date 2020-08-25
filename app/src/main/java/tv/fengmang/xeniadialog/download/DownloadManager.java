package tv.fengmang.xeniadialog.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.fengmang.xeniadialog.MyApp;

public class DownloadManager implements DownloadProgressListener {

    private final static String TAG = "DownloadManager";
    private DownloadInfo info;
    private ProgressListener progressObserver;
    private File outFile;
    private DownLoadService service;
    private long currentRead;

    private DownloadManager() {
        info = new DownloadInfo();
        //outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "yaoshi.apk");
        outFile = new File(MyApp.getInstance().getDir("Download", Context.MODE_PRIVATE), "yaoshi.apk");
        info.setSavePath(outFile.getAbsolutePath());
    }

    public static DownloadManager getInstance() {
        return Holder.manager;
    }

    public static class Holder {
        private static DownloadManager manager = new DownloadManager();
    }

    @Override
    public void progress(long read, final long contentLength, final boolean done) {
        Log.e("progress : ", "read = " + read + "contentLength = " + contentLength);
        // 该方法仍然是在子线程，如果想要调用进度回调，需要切换到主线程，否则的话，会在子线程更新UI，直接错误
        // 如果断电续传，重新请求的文件大小是从断点处到最后的大小，不是整个文件的大小，info中的存储的总长度是
        // 整个文件的大小，所以某一时刻总文件的大小可能会大于从某个断点处请求的文件的总大小。此时read的大小为
        // 之前读取的加上现在读取的
        if (info.getContentLength() > contentLength) {
            read = read + (info.getContentLength() - contentLength);
        } else {
            info.setContentLength(contentLength);
        }
        info.setReadLength(read);

        Observable.just(1).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer integer) {
                if (progressObserver != null) {
                    progressObserver.progressChanged(info.getReadLength(), info.getContentLength(), done);
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 开始下载
     *
     * @param url
     */
    public void start(String url) {
        info.setUrl(url);
        final DownloadInterceptor interceptor = new DownloadInterceptor(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(8, TimeUnit.SECONDS);
        builder.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(CommonUtils.getBasUrl(url))
                .build();
        if (service == null) {
            service = retrofit.create(DownLoadService.class);
            info.setService(service);
        } else {
            service = info.getService();
        }
        downLoad();
    }

    /**
     * 开始下载
     */
    @SuppressLint("CheckResult")
    private void downLoad() {
        service.download("bytes=" + info.getReadLength() + "-", info.getUrl())
                .map(new Function<ResponseBody, DownloadInfo>() {
                    @Override
                    public DownloadInfo apply(ResponseBody responseBody) throws Exception {
                        try {
                            //写入文件
                            FileUtil.writeCache(responseBody, new File(info.getSavePath()), info);
                        } catch (IOException e) {
                            Log.e("异常:", e.toString());
                        }
                        return info;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadInfo>() {
                    @Override
                    public void accept(DownloadInfo downloadInfo) throws Exception {
                        Log.i(TAG, "下载:" + downloadInfo.toString());
                        Log.i(TAG, "主线程否:" + (Looper.getMainLooper() == Looper.myLooper()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("下载异常:", throwable.toString());
                        Log.e(TAG, "主线程否:" + (Looper.getMainLooper() == Looper.myLooper()));
                    }
                });
    }


    /**
     * 暂停下载
     */
    public void pause() {
    }

    /**
     * 继续下载
     */
    public void reStart() {
        downLoad();
    }

    /**
     * 进度监听
     */
    public interface ProgressListener {
        void progressChanged(long read, long contentLength, boolean done);
    }

    public void setProgressListener(ProgressListener progressObserver) {
        this.progressObserver = progressObserver;
    }
}

