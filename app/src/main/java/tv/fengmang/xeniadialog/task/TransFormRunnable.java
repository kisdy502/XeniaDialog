package tv.fengmang.xeniadialog.task;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.net.LiveRetrofit;

/**
 * Created by Administrator on 2020/2/12 0012.
 */

public class TransFormRunnable implements Runnable {
    private Context mContext;
    private BaseLiveData mCurrentX;

    public TransFormRunnable(Context context) {
        this.mContext = context;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        //asset2X();
        getX();
        long end = System.currentTimeMillis();
        long coust = end - start;
        ELog.e("transForm", "asset2X方法 coust:" + coust + "毫秒");

        start = System.currentTimeMillis();
        writeData2File();
        end = System.currentTimeMillis();
        coust = end - start;
        ELog.e("transForm", "writeData2File方法coust:" + coust + "毫秒");


        start = System.currentTimeMillis();
        file2X();
        end = System.currentTimeMillis();
        coust = end - start;
        ELog.e("transForm", "file2X 方法coust:" + coust + "毫秒");

    }

    public void getX() {
        Call<BaseLiveData> xCall = LiveRetrofit.getInstance().getApi().getBaseData("1", "1");
        try {
            Response<BaseLiveData> response = xCall.execute();
            if (response.isSuccessful()) {
                BaseLiveData x = response.body();
                mCurrentX = x;
                printX(x);
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
        mCurrentX = x;
        printX(x);
    }


    public void writeData2File() {
        if (mCurrentX == null) {
            return;
        }
        File dir = mContext.getDir("jsonDir", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString = gson.toJson(mCurrentX);

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
            ELog.d("transForm", "json数据写入到文件成功!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void file2X() {
        File dir = mContext.getDir("jsonDir", Context.MODE_PRIVATE);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(dir, "base_channel_data.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileInputStream == null) {
            return;
        }
        BaseLiveData x = new Gson().fromJson(new InputStreamReader(fileInputStream), BaseLiveData.class);

        if (x == null) {
            return;
        }

        printX(x);
    }

    private void printX(BaseLiveData x) {
        int status = x.getStatus();
        String message = x.getMsg();
        String version = x.getVersion();
        Log.d("transForm", status + "," + message + "," + version);
        List<BaseLiveData.Channel> channelListBeanList = x.getChannelList();
        BaseLiveData.DefaultBootChannel defaultBootChannelBean = x.getDefaultBootChannel();
        List<BaseLiveData.TvClass> tvclassListBeanList = x.getTvclassList();

        if (defaultBootChannelBean != null) {
            ELog.d("transForm", "defaultBootChannelBean:" + defaultBootChannelBean.toString());
        }

        if (tvclassListBeanList == null || tvclassListBeanList.isEmpty()) {
            ELog.d("transForm", "tvclassListBeanList is null");
        } else {
            ELog.i("transForm", "TvClass");
            for (BaseLiveData.TvClass bean : tvclassListBeanList) {
                ELog.d("transForm", bean.toString());
            }
        }

        if (channelListBeanList == null || channelListBeanList.isEmpty()) {
            ELog.d("transForm", "channelListBeanList is null");
        } else {
            ELog.i("transForm", "Channel");
            for (BaseLiveData.Channel bean : channelListBeanList) {
                ELog.d("transForm", bean.toString());
            }
        }
    }
}
