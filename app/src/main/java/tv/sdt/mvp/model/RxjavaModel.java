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
import tv.sdt.mvp.contract.RxjavaContract;

public class RxjavaModel implements RxjavaContract.Model {

    private final static String TAG = "LiveModel";
    private Context mContext;

    public RxjavaModel(Context context) {
        this.mContext = context;
    }


}
