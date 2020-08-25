package tv.fengmang.xeniadialog.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChannelProgram {
    @SerializedName("channelId")
    private int channelId;
    @SerializedName("scheduleList")
    private List<LiveProgram> programList;

    public ChannelProgram() {
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public List<LiveProgram> getProgramList() {
        return programList;
    }

    public void setProgramList(List<LiveProgram> programList) {
        this.programList = programList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}