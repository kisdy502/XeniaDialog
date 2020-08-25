package tv.fengmang.xeniadialog.db;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = DbLive.class, name = "TbFavoriteChannel")
public class TbFavoriteChannel extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private int HD;
    @Column
    private int channelId;
    @Column
    private String name;
    @Column
    private int remoteNo;
    @Column
    private String replayCode;
    @Column
    private String channelImg;
    @Column
    private String channelCate;
    @Column
    private String province;
    @Column
    private String uid;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
