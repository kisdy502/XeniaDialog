package tv.sdt.mvp.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Response;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.db.LiveConfig;
import tv.fengmang.xeniadialog.db.LiveConfig_Table;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.log.IOUtils;
import tv.fengmang.xeniadialog.net.LiveRetrofit;
import tv.sdt.mvp.contract.LiveContract;

public class LiveModel implements LiveContract.Model {
    private final static String TAG = "LiveModel";
    private Context mContext;

    public LiveModel(Context context) {
        this.mContext = context;
    }

    @Override
    public Observable<BaseLiveData> loadBaseDataFromAsset() {
        Observable<BaseLiveData> observable = Observable.create(new ObservableOnSubscribe<BaseLiveData>() {
            @Override
            public void subscribe(ObservableEmitter<BaseLiveData> emitter) throws Exception {
                BaseLiveData baseLiveData = null;
                InputStream inputStream = null;
                try {
                    inputStream = mContext.getResources().getAssets().open("base_channel_data.json");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (inputStream == null) {
                    emitter.onError(new NullPointerException("read assets return null"));
                    return;
                }
                baseLiveData = new Gson().fromJson(new InputStreamReader(inputStream), BaseLiveData.class);
                IOUtils.close(inputStream);
                if (baseLiveData == null) {
                    emitter.onComplete();
                } else {
                    Log.d("test", "read base data from assets file:");
                    emitter.onNext(baseLiveData);
                }
            }
        });
        return observable;
    }

    @Override
    public Observable<BaseLiveData> loadBaseDataFromDisk() {
        Observable<BaseLiveData> observable = Observable.create(new ObservableOnSubscribe<BaseLiveData>() {
            @Override
            public void subscribe(ObservableEmitter<BaseLiveData> emitter) throws Exception {
                BaseLiveData baseLiveData = null;
                File dir = mContext.getDir("jsonDir", Context.MODE_PRIVATE);
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(new File(dir, "base_channel_data.json"));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (fileInputStream == null) {
                    emitter.onError(new NullPointerException("read disk file return null"));
                    return;
                }
                baseLiveData = new Gson().fromJson(new InputStreamReader(fileInputStream), BaseLiveData.class);
                IOUtils.close(fileInputStream);
                if (baseLiveData == null) {
                    emitter.onComplete();
                } else {
                    Log.d(TAG, "read base data from disk file:");
                    emitter.onNext(baseLiveData);
                }
            }
        });

        return observable;
    }

    @Override
    public BaseLiveData getBaseDataFromDisk() {
        BaseLiveData baseLiveData = null;
        File dir = mContext.getDir("jsonDir", Context.MODE_PRIVATE);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(dir, "base_channel_data.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fileInputStream == null) {
            return baseLiveData;
        }
        baseLiveData = new Gson().fromJson(new InputStreamReader(fileInputStream), BaseLiveData.class);
        IOUtils.close(fileInputStream);
        return baseLiveData;
    }

    @Override
    public BaseLiveData getBaseDataFromAsset() {
        BaseLiveData baseLiveData = null;
        InputStream inputStream = null;
        try {
            inputStream = mContext.getResources().getAssets().open("base_channel_data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            return baseLiveData;
        }
        baseLiveData = new Gson().fromJson(new InputStreamReader(inputStream), BaseLiveData.class);
        IOUtils.close(inputStream);
        return baseLiveData;
    }

    @Override
    public Observable<BaseLiveData> loadBaseDataFromNet() {
        Observable<BaseLiveData> observable = Observable.create(new ObservableOnSubscribe<BaseLiveData>() {
            @Override
            public void subscribe(ObservableEmitter<BaseLiveData> emitter) {
                LiveConfig liveConfig = SQLite.select().from(LiveConfig.class).where(LiveConfig_Table.key.eq(LiveConfig.KEY_BASEDATA_VERSION)).querySingle();
                String version = null;
                if (liveConfig == null || TextUtils.isEmpty(liveConfig.getValue())) {
                    version = "0";
                } else {
                    version = liveConfig.getValue();
                }
                Call<BaseLiveData> call = LiveRetrofit.getInstance().getApi().getBaseData("1", version);
                Response<BaseLiveData> response = null;
                try {
                    response = call.execute();
                    if (response.isSuccessful()) {
                        BaseLiveData baseLiveData = response.body();
                        if (baseLiveData == null || version.equalsIgnoreCase(baseLiveData.getVersion())) {
                            emitter.onComplete();
                        } else {
                            if (liveConfig == null) {
                                liveConfig = new LiveConfig();
                            }
                            liveConfig.setKey(LiveConfig.KEY_BASEDATA_VERSION);
                            liveConfig.setValue(baseLiveData.getVersion());
                            liveConfig.save();
                            writeBaseData2File(baseLiveData);
                            Log.d("test", "read base data from network:");
                            emitter.onNext(baseLiveData);
                        }
                    } else {
                        Log.d(TAG, "code:" + response.code());
                        emitter.onError(new IOException("failed response code:" + response.code()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        });
        return observable;
    }

    @Override
    public void writeBaseData2File(BaseLiveData baseLiveData) {
        File dir = mContext.getDir("jsonDir", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = gson.toJson(baseLiveData);

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(dir, "base_channel_data.json"), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (fileWriter == null) {
            return;
        }
        try {
            fileWriter.write(jsonString);
            fileWriter.flush();
            ELog.d(TAG, "json数据写入到文件成功!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fileWriter);

        }
    }
}
