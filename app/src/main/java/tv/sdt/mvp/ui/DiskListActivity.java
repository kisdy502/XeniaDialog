package tv.sdt.mvp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.adapter.DiskAdapter;
import tv.fengmang.xeniadialog.utils.DiskHelper;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;


public class DiskListActivity extends AppCompatActivity {

    private static final String TAG = "DiskListActivity";
    List<DiskHelper.StorageInfo> diskList;
    MenuRecyclerView rvDiskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_list);
        rvDiskList = findViewById(R.id.rv_disk);
        setTitle("本机磁盘列表");
        findViewById(R.id.btn_to_root_dir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/");
                if (!file.canRead()) {
                    Toast.makeText(getApplicationContext(), "无法访问磁盘", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(DiskListActivity.this, FileListActivity.class);
                intent.putExtra("path", "/");
                startActivity(intent);
            }
        });
        initList();
    }

    private void initList() {
        diskList = DiskHelper.getStoragePath(getApplicationContext());
        if (diskList == null || diskList.isEmpty()) {
            return;
        }

        for (DiskHelper.StorageInfo disk : diskList) {
            Log.d(TAG, "disk:" + disk.toString());
        }

        rvDiskList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        DiskAdapter diskAdapter = new DiskAdapter(getApplicationContext(), diskList);
        rvDiskList.setAdapter(diskAdapter);

        rvDiskList.setOnItemClickListener(new MenuRecyclerView.ItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                DiskHelper.StorageInfo storageInfo = diskList.get(position);
                String path = storageInfo.getPath();

                File file = new File(path);
                if (!file.canRead()) {
                    Toast.makeText(getApplicationContext(), "无法访问磁盘", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(path)) {
                    //TODO 提示无法打开
                    Log.d(TAG, "path is empty");
                } else {
                    Intent intent = new Intent(DiskListActivity.this, FileListActivity.class);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }
            }
        });
    }
}
