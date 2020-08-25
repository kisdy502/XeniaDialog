package tv.fengmang.xeniadialog.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveProgram{
    /**
     * name : 知青家庭第10集
     * startTime : 2018-07-24 00:27:00
     * endTime : 2018-07-24 01:11:00
     */

    @SerializedName("name")
    private String name;
    @SerializedName("startTime")
    private String startTime;
    @SerializedName("endTime")
    private String endTime;

    /**
     * 0 回看 1 正在播放 2 未预约 3 已预约
     */
    @Expose
    private int type;
    @Expose
    private String date;
    @Expose
    private String timeHorizon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeHorizon() {
        return timeHorizon;
    }

    public void setTimeHorizon(String timeHorizon) {
        this.timeHorizon = timeHorizon;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public LiveProgram() {
    }
}