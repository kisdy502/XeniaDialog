package tv.fengmang.xeniadialog.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import tv.fengmang.xeniadialog.log.ELog;

/**
 * 三种颜色选中模式/菜单模式
 * 该设计方式，有个很郁闷的地方，水平滚动，水平方向不能丢失焦点，
 */
public class MenuRecyclerView extends RecyclerView {

    protected int mFocusedPosition = 0;
    protected int mSelectedPosition = 0;
    private int mOrientation;
    private int mSelectedItemOffsetStart, mSelectedItemOffsetEnd;   //滚动到中部位置

    private boolean isMenuMode = true;

    //是否可以纵向移出
    private boolean mCanFocusOutVertical = true;
    //是否可以横向移出
    private boolean mCanFocusOutHorizontal = true;

    private String viewName = MenuRecyclerView.class.getSimpleName();

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public MenuRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public MenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
        //setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
    }

    @Override
    public void onChildAttachedToWindow(@NonNull final View child) {
        super.onChildAttachedToWindow(child);
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(child, getChildViewHolder(child).getAdapterPosition());
                }
            }
        });

        child.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View view, boolean hasFocus) {
                final int position = getChildViewHolder(view).getAdapterPosition();

                if (hasFocus) {
                    mFocusedPosition = position;
                    if (mItemFocusListener != null) {
                        mItemFocusListener.onItemFocused(view, position);
                    }

                    if (view.isSelected()) {
                        view.setSelected(false);
                        if (mItemSelectedListener != null) {
                            mItemSelectedListener.onItemUnSelected(view, mFocusedPosition);
                        }
                    }
                } else {
                    if (mItemFocusListener != null) {
                        mItemFocusListener.onItemLostFocus(view, position);
                    }
                }
            }
        });

        child.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemLongClickListener != null) {
                    return mItemLongClickListener.onItemLongClick(child, getChildViewHolder(view).getAdapterPosition());
                }
                return false;
            }
        });
    }

    @Override
    public void onChildDetachedFromWindow(@NonNull View child) {
        super.onChildDetachedFromWindow(child);
        child.setOnClickListener(null);
        child.setOnLongClickListener(null);
        child.setOnFocusChangeListener(null);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        if (getLayoutManager() == null) {
            return false;
        }
        View lastFocusedView = getLayoutManager().findViewByPosition(mFocusedPosition);
        if (lastFocusedView != null) {
            lastFocusedView.requestFocus();
        } else {
            lastFocusedView = getLayoutManager().findViewByPosition(getFirstVisiblePosition());
            if (lastFocusedView != null) {
                lastFocusedView.requestFocus();
            } else {
                super.requestFocus(direction, previouslyFocusedRect);
            }
        }
        //Log.i(viewName, "requestFocus = " + mFocusedPosition);
        return false;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (null != child) {
            mSelectedItemOffsetStart = (mOrientation == HORIZONTAL) ? (getFreeWidth() - child.getWidth()) : (getFreeHeight() - child.getHeight());
            mSelectedItemOffsetStart /= 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }
        super.requestChildFocus(child, focused);    //执行过super.requestChildFocus之后hasFocus会变成true
        if (null != child) {
            //取得获得焦点的item的position
            mFocusedPosition = getChildViewHolder(child).getAdapterPosition();
            //Log.i(viewName, "requestChildFocus = " + mFocusedPosition);
        }
    }

    @Override
    public boolean isInTouchMode() {
        // 解决4.4版本抢焦点的问题
        if (Build.VERSION.SDK_INT == 19) {
            return !(hasFocus() && !super.isInTouchMode());
        } else {
            return super.isInTouchMode();
        }
    }


    /**
     * 重写这个方法，可以控制焦点框距离父容器的距离,以及由于recyclerView的滚动
     * 产生的偏移量，导致焦点框错位，这里可以记录滑动偏移量。
     * RecyclerView 版本27以及更高版本必须配合V7LinearLayoutManager 才能实现，焦点item居中效果
     */
    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        //计算出当前viewGroup即是RecyclerView的内容区域
        final int parentLeft = getPaddingLeft();
        final int parentTop = getPaddingTop();
        final int parentRight = getWidth() - getPaddingRight();
        final int parentBottom = getHeight() - getPaddingBottom();

        //计算出child,此时是获取焦点的view请求的区域
        final int childLeft = child.getLeft() + rect.left;
        final int childTop = child.getTop() + rect.top;
        final int childRight = childLeft + rect.width();
        final int childBottom = childTop + rect.height();

        //获取请求区域四个方向与RecyclerView内容四个方向的距离
        final int offScreenLeft = Math.min(0, childLeft - parentLeft - mSelectedItemOffsetStart);
        final int offScreenTop = Math.min(0, childTop - parentTop - mSelectedItemOffsetStart);
        final int offScreenRight = Math.max(0, childRight - parentRight + mSelectedItemOffsetEnd);
        final int offScreenBottom = Math.max(0, childBottom - parentBottom + mSelectedItemOffsetEnd);

        final boolean canScrollHorizontal = getLayoutManager().canScrollHorizontally();
        int dx;
        if (canScrollHorizontal) {
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                dx = offScreenRight != 0 ? offScreenRight
                        : Math.max(offScreenLeft, childRight - parentRight);
            } else {
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
            }
        } else {
            dx = 0;
        }
        int dy = offScreenTop != 0 ? offScreenTop
                : Math.min(childTop - parentTop, offScreenBottom);
        //在这里可以微调滑动的距离,根据项目的需要
        if (dx != 0 || dy != 0) {
            //Log.d(viewName, "immediate:" + immediate + ",dx" + dx + ",dy:" + dy);
            //最后执行滑动
            if (immediate) {
                scrollBy(dx, dy);
            } else {
                smoothScrollBy(dx, dy);
            }
            return true;
        }
        postInvalidate();
        return false;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager) {
            mOrientation = ((GridLayoutManager) layout).getOrientation();
        } else if (layout instanceof LinearLayoutManager) {
            mOrientation = ((LinearLayoutManager) layout).getOrientation();
        } else {
            throw new IllegalArgumentException("not support StaggeredGridLayoutManager");
        }
    }

    //实现焦点记忆的关键代码
    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus() || mFocusedPosition < 0 || getLayoutManager() == null || (view = getLayoutManager().findViewByPosition(mFocusedPosition)) == null) {
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            //将当前的view放到Focusable views列表中，再次移入焦点时会取到该view,实现焦点记忆功能
            views.add(view);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    /**
     * 控制当前焦点最后绘制，防止焦点放大后被遮挡
     * 原顺序123456789，当4是focus时，绘制顺序变为123567894
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedChild = getFocusedChild();
        //Log.i(viewName, "focusedChild =" + focusedChild);
        if (focusedChild == null) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            int index = indexOfChild(focusedChild);
            //Log.i(viewName, " index = " + index + ",i=" + i + ",count=" + childCount);
            if (i == childCount - 1) {
                return index;
            }
            if (i < index) {
                return i;
            }
            return i + 1;
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (getLayoutManager() != null) {
            View view = getLayoutManager().findViewByPosition(mSelectedPosition);
            if (view != null)
                view.setActivated(false);
            mSelectedPosition = 0;
            mFocusedPosition = 0;
        }
        super.setAdapter(adapter);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }
        if (view != null) {
            //findContainingItemView()方法返回焦点view所在的父view,如果是在recyclerview之外，就会是null.所以根据是否是null,来判断是否是移出了recyclerview
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    return focused;     //屏蔽焦点纵向移出recyclerview
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
                    return focused;     //屏蔽焦点横向移出recyclerview
                }
                int focusPosition = getChildViewHolder(focused).getAdapterPosition();
                //Log.e(viewName, "focusSearch" + focusPosition);
                //调用移出的监听
                if (!focused.isSelected()) {
                    focused.setSelected(true);
                    mSelectedPosition = focusPosition;
                    if (mItemSelectedListener != null) {
                        mItemSelectedListener.onItemSelected(focused, mSelectedPosition);
                    }
                    //onFocusChanged(false, FOCUS_DOWN, null);
                }
                if (mFocusLostListener != null) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }
                return view;
            } else {
                //Log.e(viewName, "focusSearch,in ");
            }
        }
        return view;
    }

    public int getFirstVisiblePosition() {
        if (getChildCount() == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(0));
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        //Log.d(viewName, "childCount:" + childCount);
        if (childCount == 0)
            return 0;
        else
            return getChildLayoutPosition(getChildAt(childCount - 1));
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
    }

    public int getItemCount() {
        if (null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    /**
     * 设置默认选中.
     */
    public void setDefaultSelect(final int pos) {
        //ELog.i("setDefaultSelect:" + pos);
        mSelectedPosition = pos;
        mFocusedPosition = pos;
        scrollToPosition(mSelectedPosition);
    }

    public void setDefaultSelect(final int pos, boolean smoothScroll) {
        ELog.i("setDefaultSelect:" + pos);
        mSelectedPosition = pos;
        mFocusedPosition = pos;
        if (smoothScroll) {
            smoothScrollToPosition(mSelectedPosition);
        } else {
            scrollToPosition(mSelectedPosition);
        }
        //重写了LayoutManager.smoothScrollToPosition()实现了滚动结束时，找到滚动的位置，并请求焦点
        //getLayoutManager().smoothScrollToPosition(this, new State(), mSelectedPosition);
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    private ItemClickListener mItemClickListener;
    private ItemFocusListener mItemFocusListener;
    private ItemLongClickListener mItemLongClickListener;
    private ItemSelectedListener mItemSelectedListener;
    private FocusLostListener mFocusLostListener;

    public void setOnItemSelectedListener(ItemSelectedListener itemSelectedListener) {
        this.mItemSelectedListener = itemSelectedListener;
    }

    public void setOnItemLongClickListener(ItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemFocusListener(ItemFocusListener mItemFocusListener) {
        this.mItemFocusListener = mItemFocusListener;
    }

    public void setCanFocusOutVertical(boolean mCanFocusOutVertical) {
        this.mCanFocusOutVertical = mCanFocusOutVertical;
    }

    public void setCanFocusOutHorizontal(boolean mCanFocusOutHorizontal) {
        this.mCanFocusOutHorizontal = mCanFocusOutHorizontal;
    }

    public interface ItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface ItemLongClickListener {
        boolean onItemLongClick(View itemView, int position);
    }

    public interface ItemFocusListener {
        void onItemFocused(View itemView, int position);

        void onItemLostFocus(View itemView, int position);
    }

    public interface ItemSelectedListener {
        void onItemSelected(View itemView, int position);

        void onItemUnSelected(View itemView, int position);
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

}
