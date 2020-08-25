package tv.sdt.mvp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import tv.fengmang.xeniadialog.R;
import tv.sdt.mvp.base.BaseMvpActivity;
import tv.sdt.mvp.contract.LiveContract;
import tv.sdt.mvp.presenter.LivePresenter;

public class LivePlayActivity extends BaseMvpActivity<LivePresenter> implements LiveContract.View, View.OnClickListener {
    private Button btnLoad;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected LivePresenter initPresenter() {
        return new LivePresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_live_play;
    }

    @Override
    public void initView() {
        btnLoad = findViewById(R.id.btn_load);
        pbLoading = findViewById(R.id.pb_loading);
        btnLoad.setOnClickListener(this);

    }

    @Override
    public void startRequest() {
    }

    @Override
    public void showLoading() {
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void toast(String s) {

    }

    @Override
    public void onClick(View v) {
        //mPresenter.loopLoginResult();
        mPresenter.loadProgramList();
    }
}
