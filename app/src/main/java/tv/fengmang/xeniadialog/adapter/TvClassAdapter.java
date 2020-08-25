package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.widget.JumpingView;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class TvClassAdapter extends RecyclerView.Adapter<TvClassAdapter.VH> {

    private static final String TAG = TvClassAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<BaseLiveData.TvClass> tvClassList;
    private BaseLiveData.TvClass mPlayingTvClass;

    public TvClassAdapter(Context context, List<BaseLiveData.TvClass> tvClassList, BaseLiveData.TvClass playingTvClass) {
        this.mContext = context;
        this.tvClassList = tvClassList;
        this.mPlayingTvClass = playingTvClass;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_tv_class, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        BaseLiveData.TvClass tvClass = tvClassList.get(position);
        holder.tvName.setText(tvClass.getName() + "(" + position + ")");
        if (mPlayingTvClass != null && mPlayingTvClass.getId() == tvClass.getId()) {
            holder.tvName.setTextColor(Color.parseColor("#D62708"));
            holder.jumpingView.startJump();
        } else {
            holder.tvName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.jumpingView.stopJump();
        }
    }


    @Override
    public int getItemCount() {
        return tvClassList == null ? 0 : tvClassList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private JumpingView jumpingView;

        public VH(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_class_name);
            jumpingView = itemView.findViewById(R.id.jumping_view);
        }
    }


    public void setPlayingTvClass(BaseLiveData.TvClass playingTvClass) {
        if (mPlayingTvClass == null && playingTvClass == null) {
            return;
        } else if (mPlayingTvClass != null && playingTvClass == null) {
            int playingPosition = getPlayingPosition();
            mPlayingTvClass = playingTvClass;
            if (playingPosition != -1) {
                notifyItemChanged(playingPosition);
            }
        } else if (mPlayingTvClass == null && playingTvClass != null) {
            mPlayingTvClass = playingTvClass;
            int playingPosition = getPlayingPosition();
            if (playingPosition != -1) {
                notifyItemChanged(playingPosition);
            }
        } else {
            if (isSameTvClass(mPlayingTvClass, playingTvClass)) {
                return;
            }
            int oldPosition = getPlayingPosition();
            ELog.d("oldPosition:" + oldPosition);
            mPlayingTvClass = playingTvClass;
            int newPosition = getPlayingPosition();
            ELog.d("newPosition:" + newPosition);
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            if (newPosition != -1) {
                notifyItemChanged(newPosition);
            }
        }
    }

    private boolean isSameTvClass(BaseLiveData.TvClass playingTvClass, BaseLiveData.TvClass targetTvClass) {
        return playingTvClass.getId() == targetTvClass.getId();
    }


    private int getPlayingPosition() {
        if (mPlayingTvClass == null || tvClassList == null || tvClassList.isEmpty()) {
            return -1;
        } else {
            for (int i = 0, len = tvClassList.size(); i < len; i++) {
                if (tvClassList.get(i).getId() == mPlayingTvClass.getId()) {
                    return i;
                }
            }
        }
        return -1;
    }


}
