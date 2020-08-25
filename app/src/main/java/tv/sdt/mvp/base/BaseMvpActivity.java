package tv.sdt.mvp.base;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;


/***
 *  getLayoutId
 *  setContentView();
 *  intiView
 *  initPresenter
 *  startRequest
 *  所以开始处理业务逻辑不能放到initView方法中去做，因为 Presenter对象还是空的
 */

/**
 * @author azheng
 * @date 2018/4/24.
 * * GitHub：https://github.com/RookieExaminer
 * * Email：wei.azheng@foxmail.com
 * * Description：
 */
public abstract class BaseMvpActivity<T extends BasePresenter> extends BaseActivity implements BaseView {

    protected T mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        mPresenter.attachView(this);
        startRequest();
    }


    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    /**
     * 绑定生命周期 防止MVP内存泄漏
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> AutoDisposeConverter<T> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }

    protected abstract T initPresenter();
}
