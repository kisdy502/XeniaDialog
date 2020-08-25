package tv.sdt.mvp.contract;


import java.io.IOException;

import io.reactivex.Observable;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.sdt.mvp.base.BaseView;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface LiveContract {

    interface Model {

        Observable<BaseLiveData> loadBaseDataFromAsset();

        Observable<BaseLiveData> loadBaseDataFromDisk();

        Observable<BaseLiveData> loadBaseDataFromNet();

        void writeBaseData2File(BaseLiveData baseLiveData);

        BaseLiveData getBaseDataFromDisk();

        BaseLiveData getBaseDataFromAsset();
    }

    interface View extends BaseView {

    }

    interface Presenter {
        void loadBaseData();

        void loadBaseData2();

        void getQrLoginUrl();

        void loopLoginResult();

        void loadProgramList();
    }
}
