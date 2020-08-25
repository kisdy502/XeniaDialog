package tv.sdt.mvp.presenter;

import tv.fengmang.xeniadialog.MyApp;
import tv.sdt.mvp.base.BasePresenter;
import tv.sdt.mvp.contract.RxjavaContract;
import tv.sdt.mvp.model.RxjavaModel;

public class RxjavaPresenter extends BasePresenter<RxjavaContract.View> implements RxjavaContract.Presenter {

    private final static String TAG = "RxjavaPresenter";
    private RxjavaContract.Model model;


    public RxjavaPresenter() {
        model = new RxjavaModel(MyApp.getInstance());
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
