package tv.fengmang.xeniadialog;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.fengmang.xeniadialog.bean.ProvincesBean;

public class DoXeniaActivity extends AppCompatActivity {

    private final static String TAG = "DoXeniaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_xenia);
        baseop();
    }

    private void baseop() {

        Observable.create(new ObservableOnSubscribe<ProvincesBean.Province>() {
            @Override
            public void subscribe(ObservableEmitter<ProvincesBean.Province> emitter) throws Exception {
                boolean isMain = Looper.getMainLooper() == Looper.myLooper();
                Log.e(TAG, "subscribe x1:" + isMain);
                ProvincesBean.Province province1 = new ProvincesBean.Province();
                province1.setId("1");
                province1.setName("北京");
                emitter.onNext(province1);

                Log.e(TAG, "subscribe x2");
                ProvincesBean.Province province2 = new ProvincesBean.Province();
                province2.setId("2");
                province2.setName("上海");
                emitter.onNext(province2);
                emitter.onComplete();

                Log.e(TAG, "subscribe x3");
                ProvincesBean.Province province3 = new ProvincesBean.Province();
                province3.setId("3");
                province3.setName("河北省");
                emitter.onNext(province3);

                Log.e(TAG, "subscribe x4");
                ProvincesBean.Province province4 = new ProvincesBean.Province();
                province4.setId("4");
                province4.setName("山东省");
                emitter.onNext(province3);
            }
        }).subscribeOn(Schedulers.io()) //发射事件线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ProvincesBean.Province>() {

                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ProvincesBean.Province province) {
                        boolean isMain = Looper.getMainLooper() == Looper.myLooper();
                        Log.e(TAG, "onNext:" + isMain);
                        Log.e(TAG, "onNext" + province.getName());
                        if (province.getId().equals("3")) {
                            mDisposable.dispose(); //切断，不再接受上游事件
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete");
                    }
                });
    }
}
