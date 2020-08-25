package tv.fengmang.xeniadialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import tv.fengmang.xeniadialog.widget.WebAdView;


public class WebAdActivity extends Activity {
    private String mUrl = null;
    private WebView mWebView = null;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    public void initUI() {
        mUrl = getIntent().getStringExtra("play_url");
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));

        relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        mWebView = new WebAdView(this, mUrl);
        relativeLayout.addView(mWebView);
        setContentView(relativeLayout);
    }

    @Override
    protected void onDestroy() {
        relativeLayout.removeAllViews();
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.setVisibility(View.GONE);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();

    }
}
