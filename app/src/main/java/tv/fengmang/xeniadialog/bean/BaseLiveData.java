package tv.fengmang.xeniadialog.bean;

import com.google.gson.Gson;

import java.util.List;

import tv.fengmang.xeniadialog.bean.ProvincesBean;

/**
 * 直播分类和频道数据，所有书的基础
 * Created by Administrator on 2020/2/12 0012.
 */

public class BaseLiveData {

    private String msg;
    private DefaultBootChannel defaultBootChannel;
    private String version;
    private int status;
    private List<Channel> channelList;
    private List<TvClass> tvclassList;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DefaultBootChannel getDefaultBootChannel() {
        return defaultBootChannel;
    }

    public void setDefaultBootChannel(DefaultBootChannel defaultBootChannel) {
        this.defaultBootChannel = defaultBootChannel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    public List<TvClass> getTvclassList() {
        return tvclassList;
    }

    public void setTvclassList(List<TvClass> tvclassList) {
        this.tvclassList = tvclassList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class DefaultBootChannel {
        /**
         * bootType : 1
         * startTime : 2019-12-10 00:00:21
         * endTime : 2019-12-16 23:55:21
         * channelId : 12054
         */

        private int bootType;
        private String startTime;
        private String endTime;
        private int channelId;

        public int getBootType() {
            return bootType;
        }

        public void setBootType(int bootType) {
            this.bootType = bootType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class Channel {
        /**
         * HD : 0
         * channelId : 1
         * name : CCTV-1综合
         * remoteNo : 1
         * replayCode : 608807427
         * channelImg : http://img.fengmizhibo.com/live/201809/666a1298a86644d89882ca5fe2a0b67e.png
         * channelCate : 7,10000
         * province :
         */

        private int HD;
        private int channelId;
        private String name;
        private int remoteNo;
        private String replayCode;
        private String channelImg;
        private String channelCate;
        private String province;

        public int getHD() {
            return HD;
        }

        public void setHD(int HD) {
            this.HD = HD;
        }

        public int getChannelId() {
            return channelId;
        }

        public void setChannelId(int channelId) {
            this.channelId = channelId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getRemoteNo() {
            return remoteNo;
        }

        public void setRemoteNo(int remoteNo) {
            this.remoteNo = remoteNo;
        }

        public String getReplayCode() {
            return replayCode;
        }

        public void setReplayCode(String replayCode) {
            this.replayCode = replayCode;
        }

        public String getChannelImg() {
            return channelImg;
        }

        public void setChannelImg(String channelImg) {
            this.channelImg = channelImg;
        }

        public String getChannelCate() {
            return channelCate;
        }

        public void setChannelCate(String channelCate) {
            this.channelCate = channelCate;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class TvClass {
        /**
         * id : 62
         * name : 高清
         * type : 1
         * adtype : 0
         * liveType : 0
         * url : http://img.fengmizhibo.com/live/201908/3ebc1f4d7df0411ab603e0992347a2ff.png
         */

        private int id;
        private String name;
        private int type;
        private int adtype;
        private int liveType;
        private String url;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getAdtype() {
            return adtype;
        }

        public void setAdtype(int adtype) {
            this.adtype = adtype;
        }

        public int getLiveType() {
            return liveType;
        }

        public void setLiveType(int liveType) {
            this.liveType = liveType;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TvClass)) {
                return false;
            }
            TvClass pn = (TvClass) o;
            return pn.id == this.id && pn.name.equalsIgnoreCase(this.name);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id;
            result = 31 * result + name.hashCode();
            return result;
        }
    }
}
