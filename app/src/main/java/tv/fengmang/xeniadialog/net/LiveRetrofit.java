package tv.fengmang.xeniadialog.net;

import android.os.Environment;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tv.fengmang.xeniadialog.MyApp;

public class LiveRetrofit {

    private static final int DEFAULT_TIMEOUT = 20;

    private Retrofit retrofit;

    private LiveApi mApi;

    public static LiveRetrofit getInstance() {
        return Holder.retrofit;
    }

    private LiveRetrofit() {
//        File cacheDir = null;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            cacheDir = MyApp.getInstance().getExternalCacheDir();
//        } else {
//            cacheDir = MyApp.getInstance().getCacheDir();
//        }
//        //根据友盟错误日志显示，有部分机型，在sd卡挂载时，getExternalCacheDir()返回还是为空
//        if (cacheDir == null) {
//            cacheDir = MyApp.getInstance().getCacheDir();
//        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String url = chain.request().url().toString();
                        Log.d("retrofit", "realUrl:" + url);
                        //支持添加公共请求头
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("softwareChannel", "default")
                                .addHeader("pkgName", "com.fengmizhibo.live")
                                .addHeader("version", "1.00.01")
                                .addHeader("BaseLiveData-Kds-name", "BeeLiveTV")
                                .addHeader("BaseLiveData-Kds-channel", "default")
                                .addHeader("BaseLiveData-Kds-Ver", "1.00.01")
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response orginalResponse = chain.proceed(chain.request());
                        return orginalResponse.newBuilder()
                                .removeHeader("Pragma")
                                .removeHeader("Cache-Control")
                                .header("Cache-Control", "public, max-age=" + 30)
                                .build();
                    }
                })
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                //                .cache(new Cache(new File(cacheDir, "okhttpcache"), 10 * 1024 * 1024))
                .build();

        retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://live.fengmizhibo.com")
                .build();

        mApi = retrofit.create(LiveApi.class);
    }


    public LiveApi getApi() {
        return mApi;
    }

    private final static class Holder {
        private final static LiveRetrofit retrofit = new LiveRetrofit();
    }
}
