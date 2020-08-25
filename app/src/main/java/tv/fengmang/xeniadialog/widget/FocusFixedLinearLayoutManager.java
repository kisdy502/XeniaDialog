package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import tv.fengmang.xeniadialog.log.ELog;

/**
 * 长按下键焦点乱飞bug处理
 * 该LayoutManager仅处理了垂直方向滚动的问题
 */
public class FocusFixedLinearLayoutManager extends LinearLayoutManager {

    private static final String TAG = "Lm";


    public FocusFixedLinearLayoutManager(Context context) {
        super(context);
    }

    public FocusFixedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public FocusFixedLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if (parent instanceof MenuRecyclerView) {
            return parent.requestChildRectangleOnScreen(child, rect, immediate);
        }
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
        // 这里处理上下键，如果处理左右键，使用View.FOCUS_LEFT和View.FOCUS_RIGHT
        if (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP) {
            // 报错，不能直接这么使用
            // int currentPosition = getPosition(focused);
            int currentPosition = getPosition(getFocusedChild());
            int count = getItemCount();
            int lastVisiblePosition = findLastVisibleItemPosition();

            // 当前焦点已经处第1个item（从0开始）,第0个item为文字，如果按键向上，
            // 则先将RecyclerView滚动到顶部，然后通知Fragment将焦点设置到"照片"上
            switch (direction) {
                case View.FOCUS_DOWN:
                    currentPosition++;
                    break;
                case View.FOCUS_UP:
                    currentPosition--;
                    break;
            }

            //ELog.i(TAG, "onInterceptFocusSearch: current position=" + currentPosition);
            //ELog.i(TAG, "onInterceptFocusSearch: item count=" + count);
            //ELog.i(TAG, "onInterceptFocusSearch: lastVisiblePosition=" + lastVisiblePosition);

            if (direction == View.FOCUS_DOWN && currentPosition > lastVisiblePosition) {
                ELog.d(TAG, "onInterceptFocusSearch: update...");
                scrollToPosition(currentPosition);
            }
        }
        return super.onInterceptFocusSearch(focused, direction);
    }

    /**
     * Base class which scrolls to selected view in onStop().
     */
    abstract class TvLinearSmoothScroller extends LinearSmoothScroller {


        public TvLinearSmoothScroller(Context context) {
            super(context);
        }

        /**
         * 滑动完成后,让该targetPosition 处的item获取焦点
         */
        @Override
        protected void onStop() {
            super.onStop();
            final int targetPosition = getTargetPosition();
            View targetView = findViewByPosition(targetPosition);
            ELog.d(TAG, targetPosition + "|" + (targetView == null));
            if (targetView != null) {
                targetView.requestFocus();
            }
        }
    }


    /**
     * RecyclerView的smoothScrollToPosition方法最终会执行smoothScrollToPosition
     *
     * @param recyclerView
     * @param state
     * @param position
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        ELog.e(TAG, "smoothScrollToPosition:" + position);
//        TvLinearSmoothScroller linearSmoothScroller =
//                new TvLinearSmoothScroller(recyclerView.getContext()) {
//                    @Override
//                    public PointF computeScrollVectorForPosition(int targetPosition) {
//                        return computeVectorForPosition(targetPosition);
//                    }
//                };
//        linearSmoothScroller.setTargetPosition(position);
//        startSmoothScroll(linearSmoothScroller);
        super.smoothScrollToPosition(recyclerView, state, position);
    }


    public PointF computeVectorForPosition(int targetPosition) {
        return super.computeScrollVectorForPosition(targetPosition);
    }

}



