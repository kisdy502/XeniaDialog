package tv.sdt.mvp.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.widget.MenuRecyclerView;
import tv.sdt.mvp.base.BaseMvpActivity;
import tv.sdt.mvp.contract.RxjavaContract;
import tv.sdt.mvp.presenter.RxjavaPresenter;

public class Rxjava2DemoActivity extends BaseMvpActivity<RxjavaPresenter> implements RxjavaContract.View {
    MenuRecyclerView recyclerMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RxjavaPresenter initPresenter() {
        return new RxjavaPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_rxjava2_demo;
    }

    @Override
    public void initView() {
        recyclerMenuList = findViewById(R.id.recycler_menu_list);
        recyclerMenuList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void startRequest() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void toast(String s) {

    }
}
