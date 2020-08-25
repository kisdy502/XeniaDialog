package tv.fengmang.xeniadialog;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import tv.fengmang.xeniadialog.crash.FileHelper;
import tv.fengmang.xeniadialog.crash.JCrash;
import tv.fengmang.xeniadialog.log.LogUtils;

/**
 * Created by Administrator on 2020/2/12 0012.
 */

public class MyApp extends Application {

    private static final String TAG = "MyApp";
    private static MyApp instance;

    private RefWatcher refWatcher;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        File dir = FileHelper.getExternalCrashDir(this);
        JCrash.init(this, dir);
        initFresco();
        FlowManager.init(this);
        refWatcher = LeakCanary.install(this);
        LogUtils.getInstance(this).start();
    }

    private void initFresco() {
        //图片太多的项目，如果设备空间足够，可以将磁盘缓存调整大些,否则不需要加这个config
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(AppSettings.AppFilePathDir + "/caches"))
                .setBaseDirectoryName("rsSystemPicCache").setMaxCacheSize(200 * ByteConstants.MB)
                .setMaxCacheSizeOnLowDiskSpace(100 * ByteConstants.MB)
                .setMaxCacheSizeOnVeryLowDiskSpace(50 * ByteConstants.MB)
                .setMaxCacheSize(80 * ByteConstants.MB).build();

        //可以拦截到所有的图片请求链接，下载耗时
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String url = chain.request().url().toString();
                        long st = System.nanoTime();
                        Response response = chain.proceed(chain.request());
                        long chainMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - st);
                        Log.d(TAG, "fresco download realUrl:" + url+",spend:"+chainMs);
                        return response.newBuilder().build();
                    }
                })
                .build();                                          // build on your own
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory
                .newBuilder(this, okHttpClient)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
    }


    public static RefWatcher getRefWatcher(Context context) {
        MyApp application = (MyApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e(TAG, "onLowMemory");
        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.e(TAG, "onTrimMemory:" + level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) { // 60
            ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
        }

    }
}
