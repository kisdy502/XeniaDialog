package tv.sdt.mvp.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import java.io.File;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.adapter.CrashAdapter;
import tv.fengmang.xeniadialog.crash.FileHelper;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;

public class CrashManagerActivity extends AppCompatActivity {

    private static final String TAG = "CrashManagerActivity";
    MenuRecyclerView recyclerViewTV;
    private File mDir;
    String[] fileList;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_crash_log_list);

        recyclerViewTV = findViewById(R.id.rv_file_list);
        recyclerViewTV.setLayoutManager(new LinearLayoutManager(this));
        initData();
    }

    private void initData() {
        String type = getIntent().getStringExtra("type");
        if ("debug".equalsIgnoreCase(type)) {
            mDir = FileHelper.getExternalLogDir(getApplicationContext());
            if (mDir == null) {
                mDir = FileHelper.getInnerLogDir(getApplicationContext());
            }
            setTitle("调试日志所在目录:" + mDir.getAbsolutePath());
        } else if ("crash".equalsIgnoreCase(type)) {
            mDir = FileHelper.getExternalCrashDir(getApplicationContext());
            if (mDir == null) {
                mDir = FileHelper.getInnerCrashDir(getApplicationContext());
            }
            setTitle("闪退日志所在目录:" + mDir.getAbsolutePath());
        }

        fileList = mDir.list();
        CrashAdapter crashAdapter = new CrashAdapter(this, fileList);
        recyclerViewTV.setAdapter(crashAdapter);
        recyclerViewTV.setOnItemClickListener(new MenuRecyclerView.ItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                File file = new File(mDir, fileList[position]);
                openCrashFile(file);
            }
        });
    }

    private void openCrashFile(File file) {
        Intent intent = getTextFileIntent(file.getAbsolutePath());
        ComponentName componentName = intent.resolveActivity(getPackageManager());          //写法一
        if (componentName != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "componentName not exist...");
        }
        /*
        List<ResolveInfo> activities = getPackageManager().queryIntentActivities(intent, 0);  //写法二
        Log.e(TAG, "activities.isEmpty():" + activities.isEmpty());
        boolean isValid = !activities.isEmpty();
        if (isValid) {
            startActivity(intent);
        } else {
            Log.e(TAG, "not valid...");
        }*/
    }

    // Android获取一个用于打开文本文件的intent
    public Intent getTextFileIntent(String param) {
        Intent intent = null;
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File(param));
            } else {
                uri = Uri.fromFile(new File(param));
            }
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage(getPackageName());
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "text/plain");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intent;
    }

}
