package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Administrator on 2020/2/13 0013.
 */

public class XRecyclerView extends RecyclerView {

    private String viewName;
    private int mFocusedPosition = 0;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public XRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        setFocusable(true);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        View lastFocusedView = getLayoutManager().findViewByPosition(mFocusedPosition);
        if (lastFocusedView != null) {
            lastFocusedView.requestFocus();
        } else {
            lastFocusedView = getLayoutManager().findViewByPosition(0);
            if (lastFocusedView != null) {
                lastFocusedView.requestFocus();
            } else {
                super.requestFocus(direction, previouslyFocusedRect);
            }
        }
        Log.i(viewName, "requestFocus = " + mFocusedPosition);
        return false;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);    //执行过super.requestChildFocus之后hasFocus会变成true
        if (null != child) {
            //取得获得焦点的item的position
            mFocusedPosition = getChildViewHolder(child).getAdapterPosition();
            Log.i(viewName, "requestChildFocus = " + mFocusedPosition);
        }
    }

    @Override
    public void onChildAttachedToWindow(final View child) {
        super.onChildAttachedToWindow(child);
        child.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusedPosition = getChildLayoutPosition(child);
                } else {

                }
            }
        });
    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus() || mFocusedPosition < 0 || (view = getLayoutManager().findViewByPosition(mFocusedPosition)) == null) {
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(view);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }
}
