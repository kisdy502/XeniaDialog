package tv.sdt.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import tv.sdt.mvp.base.BasePresenter;
import tv.sdt.mvp.contract.FileListContract;
import tv.sdt.mvp.model.FileListModel;
import tv.sdt.mvp.ui.FileListActivity;
import tv.sdt.mvp.ui.TextReadActivity;

public class FileListPresenter extends BasePresenter<FileListContract.View> implements FileListContract.Presenter {

    private final static String TAG = "FileListPresenter";

    private Context mContext;
    private FileListContract.Model model;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    public FileListPresenter(Context context) {
        mContext = context.getApplicationContext();
        model = new FileListModel(context.getApplicationContext());
    }

    @Override
    public void detachView() {
        super.detachView();
        mCompositeDisposable.clear();
    }

    @Override
    public void loadingFile(final File mFileDir) {
        mView.showLoading();
        if (mFileDir == null || !mFileDir.exists()) {
            mView.hideLoading();
            return;
        }

        final Observable<File[]> observable = Observable.create(new ObservableOnSubscribe<File[]>() {

            @Override
            public void subscribe(ObservableEmitter<File[]> e) throws Exception {
                File[] list = mFileDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        if (pathname.getName().startsWith(".")) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                });
                e.onNext(list);
            }
        });

        DisposableObserver<File[]> disposableObserver = new DisposableObserver<File[]>() {

            @Override
            public void onNext(File[] list) {
                mView.onLoadFileSuccess(list);
                mView.hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                Log.w(TAG, "onError...", e);
                mView.onError(e);
                mView.hideLoading();
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    public void handleFile(File file) {
        if (file.isDirectory()) {
            if (!file.canRead()) {
                mView.toast("opendir failed, Permission denied");
            } else {
                Intent intent = new Intent(mContext, FileListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("path", file.getAbsolutePath());
                mContext.startActivity(intent);
            }
        } else if (file.isFile()) {
            if (!file.canRead()) {
                mView.toast("openfile failed,Permission denied");
            } else {
                openFile(file);
            }
        } else {
            mView.toast("未知文件类型");
        }
    }


    private void openFile(File file) {
        Log.d(TAG, "open:" + file.getAbsolutePath());
        if (file.getName().endsWith(".txt")
                || file.getName().endsWith(".log")
                || file.getName().endsWith(".css")
                || file.getName().endsWith(".html")
                || file.getName().endsWith(".js")
                || file.getName().endsWith(".rc")
                || file.getName().endsWith(".json")
                || file.getName().endsWith(".config")
                || file.getName().endsWith(".prop")
                || file.getName().endsWith(".h")
                || file.getName().endsWith(".c")
                || file.getName().endsWith(".ini")
                || file.getName().endsWith(".cpp")) {
            Intent intent = new Intent(mContext, TextReadActivity.class);
            intent.putExtra("path", file.getAbsolutePath());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
            mView.toast("不支持的文件类型");
        }
    }

}
