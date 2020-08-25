package tv.fengmang.xeniadialog.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2020/2/17 0017.
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected OnItemFocusChangeListener onItemFocusChangeListener;

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onItemFocusChangeListener != null) {
                    if (hasFocus) {
                        onItemFocusChangeListener.onItemFocused(holder.itemView);
                    } else {
                        onItemFocusChangeListener.onItemLostFocus(holder.itemView);
                    }
                }
            }
        });
    }


    public interface OnItemFocusChangeListener {
        /**
         * Called when the focus state of a view has changed.
         *
         * @param child The view whose state has changed.
         */
        void onItemFocused(View child);

        void onItemLostFocus(View child);
    }
}
