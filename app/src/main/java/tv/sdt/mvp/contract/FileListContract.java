package tv.sdt.mvp.contract;


import java.io.File;

import tv.sdt.mvp.base.BaseView;

/**
 * @author azheng
 * @date 2018/6/4.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public interface FileListContract {

    interface Model {
    }

    interface View extends BaseView {

        void onLoadFileSuccess(File[] list);
    }

    interface Presenter {
        void loadingFile(File dir);

        void handleFile(File file);
    }
}
