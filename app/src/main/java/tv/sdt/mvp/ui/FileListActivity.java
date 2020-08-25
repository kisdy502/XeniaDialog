package tv.sdt.mvp.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.adapter.FileAdapter;
import tv.fengmang.xeniadialog.adapter.OperationFileAdapter;
import tv.fengmang.xeniadialog.bean.Operation;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;
import tv.fengmang.xeniadialog.widget.V7LinearLayoutManager;
import tv.sdt.mvp.base.BaseMvpActivity;
import tv.sdt.mvp.contract.FileListContract;
import tv.sdt.mvp.presenter.FileListPresenter;

public class FileListActivity extends BaseMvpActivity<FileListPresenter> implements FileListContract.View {

    private final static String TAG = "FileListActivity";

    private String initDir;
    private String mDir;
    private File mFileDir;
    private File[] fileList;
    private MenuRecyclerView rvFileList;
    private MenuRecyclerView rvMenuList;
    private FileAdapter fileAdapter;
    private View menuView;
    private boolean isShowMenu = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_file_list;
    }

    @Override
    public void initView() {
        rvFileList = findViewById(R.id.rv_file_list);
        rvFileList.setLayoutManager(new V7LinearLayoutManager(this));
        rvFileList.setOnItemClickListener(new MenuRecyclerView.ItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (fileAdapter.isNormalStatus()) {
                    File file = fileList[position];
                    mPresenter.handleFile(file);
                } else {
                    fileAdapter.clickPosition(position);
                }
            }
        });

        rvFileList.setOnItemLongClickListener(new MenuRecyclerView.ItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View itemView, int position) {
                if (fileAdapter.isNormalStatus()) {
                    fileAdapter.longClickPosition(position);
                    toast("已进入选择模式");
                }
                return true;
            }
        });

        menuView = findViewById(R.id.menu);

        rvMenuList = findViewById(R.id.rv_menu_list);
        rvMenuList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void startRequest() {
        initDir = getIntent().getStringExtra("path");
        parseIntent();
        List<Operation> operationList = Operation.initData();
        OperationFileAdapter operationFileAdapter = new OperationFileAdapter(getApplicationContext(), operationList);
        rvMenuList.setAdapter(operationFileAdapter);
    }


    private boolean toggleMenu() {
        isShowMenu = !isShowMenu;
        menuView.setVisibility(isShowMenu ? View.VISIBLE : View.GONE);
        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent......");
        setIntent(intent);
        parseIntent();
    }

    private void parseIntent() {
        mDir = getIntent().getStringExtra("path");
        Log.d(TAG, "mDir:" + mDir);
        mFileDir = new File(mDir);
        setTitle(mDir);
        mPresenter.loadingFile(mFileDir);
    }

    @Override
    public void onLoadFileSuccess(File[] list) {
        fileList = list;
        fileAdapter = new FileAdapter(getApplicationContext(), fileList);
        rvFileList.setAdapter(fileAdapter);
        if (fileList.length == 0) {
            Toast.makeText(getApplicationContext(), "目录为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                return toggleMenu();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (isShowMenu) {
            toggleMenu();
        } else {
            if (fileAdapter.isNormalStatus()) {
                if (initDir.equalsIgnoreCase(mDir)) {
                    super.onBackPressed();
                } else {
                    Intent intent = new Intent(FileListActivity.this, FileListActivity.class);
                    intent.putExtra("path", mFileDir.getParent());
                    startActivity(intent);
                }
            } else {
                fileAdapter.resetStatus();
                toast("已退出选择模式");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected FileListPresenter initPresenter() {
        return new FileListPresenter(getApplicationContext());
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
