package tv.sdt.mvp.presenter;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import tv.fengmang.xeniadialog.MyApp;
import tv.fengmang.xeniadialog.bean.BaseJson;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.bean.ChannelProgram;
import tv.fengmang.xeniadialog.db.LiveConfig;
import tv.fengmang.xeniadialog.db.LiveConfig_Table;
import tv.fengmang.xeniadialog.net.LiveRetrofit;
import tv.fengmang.xeniadialog.net.UserRetrofit;
import tv.sdt.mvp.base.BasePresenter;
import tv.sdt.mvp.contract.LiveContract;
import tv.sdt.mvp.model.LiveModel;

public class LivePresenter extends BasePresenter<LiveContract.View> implements LiveContract.Presenter {

    private final static String TAG = "LivePresenter";
    private LiveContract.Model model;
    private int LOOP_MAXCOUNT = Integer.MAX_VALUE;      //模拟无限轮询
    private int loopCount = 0;

    Disposable disposable = null;

    public LivePresenter() {
        model = new LiveModel(MyApp.getInstance());
    }

    @Override
    public void loadBaseData() {
        mView.showLoading();
        Observable<BaseLiveData> netObserable = model.loadBaseDataFromNet();
        Observable<BaseLiveData> diskObserable = model.loadBaseDataFromDisk();
        Observable<BaseLiveData> assetsObserable = model.loadBaseDataFromAsset();

        Observable.concat(netObserable, diskObserable, assetsObserable)
                .firstElement()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseLiveData>() {
                    @Override
                    public void accept(BaseLiveData o) {
                        Log.d(TAG, "o:" + o.getTvclassList().size());
                        Log.d(TAG, "o:" + o.getChannelList().size());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });


    }

    @Override
    public void getQrLoginUrl() {

    }

    @Override
    public void loopLoginResult() {
        Observer o = new Observer<BaseJson>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onNext(BaseJson result) {
                // e.接收服务器返回的数据
                ++loopCount;
                Log.d(TAG, "result:" + result.toString());
                if (loopCount == 3) {  //返回了登录的user信息结束轮询，这里简写如此
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                // 获取轮询结束信息
                ++loopCount;
                Log.e(TAG, e.toString());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };

        Observable<BaseJson> observable = UserRetrofit.getInstance().getApi().getBaseData("74FF4CE35CBD1723fo6ma");
        observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Object> objectObservable) throws Exception {
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Object o) throws Exception {
                        if (loopCount >= LOOP_MAXCOUNT) {
                            // 此处选择发送onError事件以结束轮询，因为可触发下游观察者的onError（）方法回调
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        // 若轮询次数＜4次，则发送1Next事件以继续轮询
                        // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
                        return Observable.just(1).delay(5000, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())                     // 切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  // 切换回到主线程 处理请求结果
                .subscribe(o);
    }

    @Override
    public void loadProgramList() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("tvclassId", "5");
        queryMap.put("batchChannelId", "93");
        LiveRetrofit.getInstance().getApi().getProgramList(queryMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChannelProgram>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ChannelProgram channelProgram) {
                        Log.d(TAG, channelProgram.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String getVersion() {
        LiveConfig liveConfig = SQLite.select().from(LiveConfig.class).where(LiveConfig_Table.key.eq(LiveConfig.KEY_BASEDATA_VERSION)).querySingle();
        String version = null;
        if (liveConfig == null || TextUtils.isEmpty(liveConfig.getValue())) {
            version = "0";
        } else {
            version = liveConfig.getValue();
        }
        return version;
    }

    private void saveVersion(String version) {
        LiveConfig liveConfig = new LiveConfig();
        liveConfig.setKey(LiveConfig.KEY_BASEDATA_VERSION);
        liveConfig.setValue(version);
        liveConfig.save();
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadBaseData2() {
        mView.showLoading();
        final long start = System.currentTimeMillis();
        final String version = getVersion();
        LiveRetrofit.getInstance().getApi().getBaseData2("1", version)
                .subscribeOn(Schedulers.io())                   // 在io线程进行网络请求
                .doOnNext(new Consumer<BaseLiveData>() {
                    @Override
                    public void accept(BaseLiveData baseLiveData) throws Exception {
                        Log.d(TAG, "is main Thread 1" + (Looper.getMainLooper() == Looper.myLooper()));
                        if (!version.equalsIgnoreCase(baseLiveData.getVersion())) {
                            model.writeBaseData2File(baseLiveData);
                            saveVersion(baseLiveData.getVersion());
                            return;
                        }
                        baseLiveData = model.getBaseDataFromDisk();
                        if (baseLiveData == null) {
                            baseLiveData = model.getBaseDataFromAsset(); //assets 目录内置，必不为空
                        }
                        Log.d(TAG, "baseLiveData is null:" + (baseLiveData == null));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) // 回到 io 线程去处理
                .subscribe(new Consumer<BaseLiveData>() {
                    @Override
                    public void accept(BaseLiveData baseLiveData) throws Exception {
                        long end = System.currentTimeMillis();
                        Log.d(TAG, "coust:" + (end - start));
                        mView.hideLoading();
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

    }
}
