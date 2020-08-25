package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.R;
import tv.fengmang.xeniadialog.db.TbFavoriteChannel;
import tv.fengmang.xeniadialog.log.ELog;
import tv.fengmang.xeniadialog.widget.JumpingView;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.VH> {

    private static final String TAG = ChannelAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<BaseLiveData.Channel> mChannelDataList;
    private BaseLiveData.Channel mPlayingChannel;
    private List<TbFavoriteChannel> mTbFavoriteChannelList;

    public ChannelAdapter(Context context, List<BaseLiveData.Channel> channelDataList, List<TbFavoriteChannel> favoriteChannelList, BaseLiveData.Channel playingChannel) {
        this.mContext = context;
        this.mChannelDataList = channelDataList;
        this.mTbFavoriteChannelList = favoriteChannelList;
        this.mPlayingChannel = playingChannel;
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_channel, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        BaseLiveData.Channel videoChannel = mChannelDataList.get(position);
        Log.d(TAG, "onBindViewHolder:" + videoChannel.getName());
        holder.tvName.setText(videoChannel.getName() + "(" + position + ")");
        if (mPlayingChannel != null && mPlayingChannel.getChannelId() == videoChannel.getChannelId()) {
            holder.tvName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.jumpingView.startJump();
        } else {
            holder.tvName.setTextColor(Color.parseColor("#888888"));
            holder.jumpingView.stopJump();
        }

        if (isCollectedChannel(videoChannel)) {
            holder.sdvCollected.setVisibility(View.VISIBLE);
        } else {
            holder.sdvCollected.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return mChannelDataList == null ? 0 : mChannelDataList.size();
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvName;
        private JumpingView jumpingView;
        private SimpleDraweeView sdvCollected;

        public VH(View view) {
            super(view);
            tvName = itemView.findViewById(R.id.tv_channel_name);
            jumpingView = itemView.findViewById(R.id.jumping_view);
            sdvCollected = itemView.findViewById(R.id.sdv_collected);
        }
    }

    /**
     * 设置播放的CHannel，并刷新旧，新位置UI
     */
    public void setPlayingChannel(BaseLiveData.Channel playingChannel) {

        if (mPlayingChannel == null && playingChannel == null) {
            return;
        } else if (mPlayingChannel == null && playingChannel != null) {
            mPlayingChannel = playingChannel;
            int position = getPlayingPosition();
            if (position != -1) {
                notifyItemChanged(position);
            }
        } else if (mPlayingChannel != null && playingChannel == null) {
            int position = getPlayingPosition();
            mPlayingChannel = playingChannel;
            if (position != -1) {
                notifyItemChanged(position);
            }
        } else {
            if (isSameChannel(mPlayingChannel, playingChannel)) {
                return;
            }
            int oldPosition = getPlayingPosition();
            mPlayingChannel = playingChannel;
            int newPosition = getPlayingPosition();
            if (oldPosition != -1) {
                notifyItemChanged(oldPosition);
            }
            if (newPosition != -1) {
                notifyItemChanged(newPosition);
            }
        }

        this.mPlayingChannel = playingChannel;
        int playingPosition = getPlayingPosition();
        if (playingPosition != -1) {
            notifyItemChanged(playingPosition);
        }
    }

    /**
     * 取消收藏，如果时收藏列表，需要进行删除item操作
     *
     * @param tbFavoriteChannel
     * @param deleted
     */
    public void removeTbFavoriteChannel(TbFavoriteChannel tbFavoriteChannel, boolean deleted) {
        int pos = getFavChannelPosition(tbFavoriteChannel);
        ELog.e("adapter", "ChannelList:" + mChannelDataList.size() + ",pos:" + pos);
        removeFormTbChannelList(tbFavoriteChannel);
        if (pos == -1) {
            ELog.e("adapter", "没找到频道:" + tbFavoriteChannel.getChannelId() + "," + tbFavoriteChannel.getName());
            return;
        }
        if (deleted) {
            mChannelDataList.remove(pos);
            notifyItemRemoved(pos);
        } else {
            notifyItemChanged(pos);
        }
    }


    public void addTbFavoriteChannel(TbFavoriteChannel tbFavoriteChannel) {
        mTbFavoriteChannelList.add(tbFavoriteChannel);
        int pos = getFavChannelPosition(tbFavoriteChannel);
        notifyItemChanged(pos);
    }

    private void removeFormTbChannelList(TbFavoriteChannel tbFavoriteChannel) {
        if (mTbFavoriteChannelList == null || mTbFavoriteChannelList.isEmpty()) {
            return;
        }
        int pos = -1;
        for (int i = 0, size = mTbFavoriteChannelList.size(); i < size; i++) {
            if (mTbFavoriteChannelList.get(i).getChannelId() == tbFavoriteChannel.getChannelId()) {
                pos = i;
            }
        }
        if (pos != -1) {
            mTbFavoriteChannelList.remove(pos);
        }
    }


    private boolean isSameChannel(BaseLiveData.Channel playingChannel, BaseLiveData.Channel targetChannel) {
        return playingChannel.getChannelId() == targetChannel.getChannelId();
    }


    /**
     * 要收藏/取消收藏的频道在频道List中的位置
     *
     * @param tbFavoriteChannel
     * @return
     */
    private int getFavChannelPosition(TbFavoriteChannel tbFavoriteChannel) {
        if (tbFavoriteChannel == null || mChannelDataList == null || mChannelDataList.isEmpty()) {
            return -1;
        } else {
            for (int i = 0, len = mChannelDataList.size(); i < len; i++) {
                if (mChannelDataList.get(i).getChannelId() == tbFavoriteChannel.getChannelId()) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int getPlayingPosition() {
        if (mPlayingChannel == null || mChannelDataList == null || mChannelDataList.isEmpty()) {
            return -1;
        } else {
            for (int i = 0, len = mChannelDataList.size(); i < len; i++) {
                if (mChannelDataList.get(i).getChannelId() == mPlayingChannel.getChannelId()) {
                    return i;
                }
            }
        }

        return -1;
    }

    private boolean isCollectedChannel(BaseLiveData.Channel channel) {
        if (mTbFavoriteChannelList == null || mTbFavoriteChannelList.isEmpty()) {
            return false;
        }

        for (TbFavoriteChannel tbFavoriteChannel : mTbFavoriteChannelList) {
            if (tbFavoriteChannel.getChannelId() == channel.getChannelId()) {
                return true;
            }
        }
        return false;
    }
}
