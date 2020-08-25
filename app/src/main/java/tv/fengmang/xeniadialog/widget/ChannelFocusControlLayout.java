package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import tv.fengmang.xeniadialog.R;

/**
 * Created by Administrator on 2020/2/13 0013.
 */
public class ChannelFocusControlLayout extends RelativeLayout {

    private final static String TAG = "FocusControlLayout";

    private MenuRecyclerView recyclerTvClass;
    private MenuRecyclerView recyclerProvices;
    private MenuRecyclerView recyclerChannel;
    private SimpleDraweeView sdvUserIcon;

    public ChannelFocusControlLayout(Context context) {
        this(context, null);
    }

    public ChannelFocusControlLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ChannelFocusControlLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        //Log.e("control", "focusSearch");
        if (recyclerTvClass == null) {
            recyclerTvClass = findViewById(R.id.recycler_tv_class);
            recyclerProvices = findViewById(R.id.recycler_provinces);
            recyclerChannel = findViewById(R.id.recycler_channel);
            sdvUserIcon = findViewById(R.id.sdv_login_icon);
        }

        if (recyclerTvClass.hasFocus()) {
            if (direction == View.FOCUS_RIGHT) {
                if (recyclerProvices.getVisibility() == VISIBLE) {
                    //Log.e("control", "focusSearch x1");
                    View child = recyclerProvices.getLayoutManager().findViewByPosition(recyclerProvices.getSelectedPosition());
                    if (child != null) {
                        return child;
                    }
                    return recyclerProvices;
                } else if (recyclerChannel.getVisibility() == VISIBLE) {
                    //Log.e("control", "focusSearch x2");
                    View child = recyclerChannel.getLayoutManager().findViewByPosition(recyclerChannel.getSelectedPosition());
                    if (child != null) {
                        return child;
                    }
                    return recyclerChannel;
                } else {
                    //Log.e("control", "focusSearch x3");
                    //super.focusSearch(focused, direction);
                }
            }
        } else if (recyclerProvices.hasFocus()) {
            if (direction == View.FOCUS_LEFT) {
                View child = recyclerTvClass.getLayoutManager().findViewByPosition(recyclerTvClass.getSelectedPosition());
                if (child != null) {
                    return child;
                }
                return recyclerTvClass;
            } else if (direction == View.FOCUS_RIGHT) {
                View child = recyclerChannel.getLayoutManager().findViewByPosition(recyclerChannel.getSelectedPosition());
                if (child != null) {
                    return child;
                }
                return recyclerChannel;
            }
        } else if (recyclerChannel.hasFocus()) {
            if (direction == View.FOCUS_LEFT) {
                if (recyclerProvices.getVisibility() == VISIBLE) {
                    View child = recyclerProvices.getLayoutManager().findViewByPosition(recyclerProvices.getSelectedPosition());
                    if (child != null) {
                        return child;
                    }
                    return recyclerProvices;
                } else {
                    View child = recyclerTvClass.getLayoutManager().findViewByPosition(recyclerTvClass.getSelectedPosition());
                    if (child != null) {
                        return child;
                    }
                    return recyclerTvClass;
                }
            }
        } else if (sdvUserIcon.hasFocus()) {
            if (direction == View.FOCUS_UP) {
                return sdvUserIcon;
            }
        }
        return super.focusSearch(focused, direction);

    }
}
