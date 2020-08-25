package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2020/2/11 0011.
 */

public class WebAdView extends WebView {

    private String url;

    public interface OnWebCloseListener {
        public void onClose();
    }

    private OnWebCloseListener listener;

    public WebAdView(Context context, String url) {
        super(context);
        this.url = url;
        init();
    }

    public void setOnWebCloseListener(OnWebCloseListener l) {
        listener = l;
    }

    private void init() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels, dm.heightPixels);
        setLayoutParams(params);

        getSettings().setJavaScriptEnabled(true);
        clearCache(true);
        WebSettings settings = getSettings();

        setBackgroundColor(0x00000000);
        setBackgroundResource(0);

        if (Build.VERSION.SDK_INT < 19) {
            if (!settings.getLayoutAlgorithm().equals(WebSettings.LayoutAlgorithm.SINGLE_COLUMN)) {
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            }
        } else {
            if (!settings.getLoadWithOverviewMode()) {
                settings.setLoadWithOverviewMode(true);
            }
            if (!settings.getUseWideViewPort()) {
                settings.setUseWideViewPort(true);
            }
        }

        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setAlpha(0.0f);
                setVisibility(View.VISIBLE);
                animate().alpha(1.0f).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                setVisibility(View.INVISIBLE);
            }
        });

        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        requestFocus();

        loadURL();
    }

    private void loadURL() {
        if (null != url) {
            loadUrl(url);
        } else {
            setVisibility(View.INVISIBLE);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }

        final int keycode = event.getKeyCode();
        if (keycode == KeyEvent.KEYCODE_MENU) {
            loadURL();
            return true;
        }
        if (keycode == KeyEvent.KEYCODE_BACK) {
            if (canGoBack()) {
                goBack();
                return true;
            } else if (listener != null) {
                listener.onClose();
                return true;
            }

        }
        return super.dispatchKeyEvent(event);
    }
}
