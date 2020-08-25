package tv.fengmang.xeniadialog.task;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import retrofit2.Call;
import retrofit2.Response;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.net.LiveRetrofit;

/**
 * Created by Administrator on 2020/2/12 0012.
 */

public class GetBaseDataRunnable implements Runnable {

    private Context mContext;
    private BaseLiveData mBaseLiveData;

    public GetBaseDataRunnable(Context context) {
        this.mContext = context;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        requestX();
        long end = System.currentTimeMillis();
        long coust = end - start;
        ELog.e("transForm", "asset2X方法 coust:" + coust + "毫秒");
    }

    public BaseLiveData getBaseData() {
        return mBaseLiveData;
    }

    public void requestX() {
        Call<BaseLiveData> xCall = LiveRetrofit.getInstance().getApi().getBaseData("1", "1");
        try {
            Response<BaseLiveData> response = xCall.execute();
            if (response.isSuccessful()) {
                BaseLiveData baseLiveData = response.body();
                mBaseLiveData = baseLiveData;
            } else {
                ELog.d("test", "code:" + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void asset2X() {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getResources().getAssets().open("base_channel_data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (inputStream == null) {
            return;
        }

        BaseLiveData x = new Gson().fromJson(new InputStreamReader(inputStream), BaseLiveData.class);

        if (x == null) {
            return;
        }
        mBaseLiveData = x;
    }


}
