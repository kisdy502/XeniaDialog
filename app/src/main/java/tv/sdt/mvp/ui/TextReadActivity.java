package tv.sdt.mvp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.adapter.TextContentAdapter;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;
import tv.fengmang.xeniadialog.widget.RecyclerViewTV;
import tv.fengmang.xeniadialog.widget.V7LinearLayoutManager;

public class TextReadActivity extends AppCompatActivity {

    private final static String TAG = "TextReadActivity";
    private File mFile;
    private String mPath;
    private MenuRecyclerView rvContent;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_content);
        rvContent = findViewById(R.id.rv_content);

        initReadContent();
    }


    private static File getFileFromContentUri(Uri contentUri, Context context) {
        File rootDataDir = context.getFilesDir();
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(context, contentUri, copyFile);
            return copyFile;
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    /**
     * 大文件拷贝是耗时操作，会引起界面卡顿
     *
     * @param context
     * @param srcUri
     * @param dstFile
     */
    public static void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 4;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return count;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }


    private void initReadContent() {

        Observable<List<String>> observable = Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                Intent intent = getIntent();
                if (intent.hasExtra("path")) {
                    mPath = intent.getStringExtra("path");
                    mFile = new File(mPath);
                } else {
                    Uri uri = intent.getData();
                    if (uri != null) {
                        mPath = uri.getPath();                          // 访问路劲
                        Log.e(TAG, "path: " + mPath);
                        String url = uri.toString();                          // 完整的url信息
                        Log.e(TAG, "url: " + uri);
                        String scheme = uri.getScheme();                      // scheme部分
                        Log.e(TAG, "scheme: " + scheme);

                        setTitle(mPath);
                        if (Build.VERSION.SDK_INT >= 24) {
                            mFile = getFileFromContentUri(uri, getApplicationContext());  //7.0大文件耗时，放到子线程去做
                        } else {
                            mFile = new File(mPath);
                        }
                    }
                }

                return Observable.just(mPath);
            }
        }).flatMap(new Function<String, ObservableSource<List<String>>>() {

            @Override
            public ObservableSource<List<String>> apply(String s) throws Exception {
                return getContentListObservable(s);
            }
        });


        DisposableObserver<List<String>> observer = new DisposableObserver<List<String>>() {

            @Override
            public void onNext(List<String> list) {
                setTitle(mPath);
                if (list == null || list.isEmpty()) {
                    return;
                }
                rvContent.setLayoutManager(new V7LinearLayoutManager(getApplicationContext()));
                TextContentAdapter textContentAdapter = new TextContentAdapter(getApplicationContext(), list);
                rvContent.setAdapter(textContentAdapter);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError...", e);
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer);
    }

    private Observable<List<String>> getContentListObservable(final String path) {
        return Observable.create(new ObservableOnSubscribe<List<String>>() {

            @Override
            public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {

                Log.d(TAG, "getContentListObservable:" + path);
                if (mFile == null || !mFile.exists()) {
                    emitter.onNext(new ArrayList<String>());
                    emitter.onComplete();
                } else {

                    InputStream instream = null;
                    try {
                        List<String> list = new ArrayList<>(1024);
                        instream = new FileInputStream(mFile);
                        if (instream != null) {
                            InputStreamReader inputreader = new InputStreamReader(instream);
                            BufferedReader buffreader = new BufferedReader(inputreader);
                            String line;
                            //分行读取
                            while ((line = buffreader.readLine()) != null) {
                                list.add(line);
                            }
                            emitter.onNext(list);
                        }
                    } catch (java.io.FileNotFoundException fe) {
                        Log.w(TAG, "The File doesn't not exist.", fe);
                    } catch (IOException ioe) {
                        Log.w(TAG, "io exception", ioe);
                    } finally {
                        try {
                            instream.close();
                        } catch (Exception ee) {
                        }
                    }
                    emitter.onComplete();
                }
            }

        });
    }


}
