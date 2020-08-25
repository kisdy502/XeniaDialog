package tv.fengmang.xeniadialog.db;

import java.util.ArrayList;
import java.util.List;

import tv.fengmang.xeniadialog.bean.BaseLiveData;

public class BeanAdapter {

    public static BaseLiveData.Channel fromTbFavChannel(TbFavoriteChannel tbFavoriteChannel) {
        BaseLiveData.Channel channel = new BaseLiveData.Channel();
        channel.setChannelId(tbFavoriteChannel.getChannelId());
        channel.setChannelCate(tbFavoriteChannel.getChannelCate());
        channel.setChannelImg(tbFavoriteChannel.getChannelImg());
        channel.setHD(tbFavoriteChannel.getHD());
        channel.setProvince(tbFavoriteChannel.getProvince());
        channel.setName(tbFavoriteChannel.getName());
        channel.setRemoteNo(tbFavoriteChannel.getRemoteNo());
        channel.setReplayCode(tbFavoriteChannel.getReplayCode());

        return channel;
    }

    public static TbFavoriteChannel fromChannelBean(BaseLiveData.Channel channel) {
        TbFavoriteChannel tbFavoriteChannel = new TbFavoriteChannel();
        tbFavoriteChannel.setChannelId(channel.getChannelId());
        tbFavoriteChannel.setChannelCate(channel.getChannelCate());
        tbFavoriteChannel.setChannelImg(channel.getChannelImg());
        tbFavoriteChannel.setHD(channel.getHD());
        tbFavoriteChannel.setProvince(channel.getProvince());
        tbFavoriteChannel.setName(channel.getName());
        tbFavoriteChannel.setRemoteNo(channel.getRemoteNo());
        tbFavoriteChannel.setReplayCode(channel.getReplayCode());

        return tbFavoriteChannel;
    }


    public static List<BaseLiveData.Channel> fromTbFavChannelList(List<TbFavoriteChannel> tbFavoriteChannelList) {
        List<BaseLiveData.Channel> channelList = new ArrayList<>(tbFavoriteChannelList.size());
        for (TbFavoriteChannel tbFavoriteChannel : tbFavoriteChannelList) {
            BaseLiveData.Channel channel = fromTbFavChannel(tbFavoriteChannel);
            channelList.add(channel);
        }
        return channelList;
    }
}
