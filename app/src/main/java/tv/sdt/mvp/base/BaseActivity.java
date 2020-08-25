package tv.sdt.mvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * @author azheng
 * @date 2018/4/24.
 * GitHub：https://github.com/RookieExaminer
 * Email：wei.azheng@foxmail.com
 * Description：
 */
public abstract class BaseActivity extends AppCompatActivity {


    //private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        //unbinder = ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        //unbinder.unbind();
        super.onDestroy();
    }

    /**
     * 设置布局id
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化View对象，设置linstener等可以放在这里做
     */
    public abstract void initView();

    /**
     * 开始调用业务请求，
     */
    public abstract void startRequest();

}
