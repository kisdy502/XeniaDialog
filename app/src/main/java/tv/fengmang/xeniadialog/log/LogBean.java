package tv.fengmang.xeniadialog.log;


/**
 * Created by chenran3 on 2018/1/3.
 */

class LogBean {
    public float time;
    public String timeStr = "";
    public String msg = "";
    public int level = 0;
    public String tag = "";

    public LogBean() {
    }


    public String toString() {
        StringBuffer sb = new StringBuffer(this.timeStr);
        sb.append(' ').append(this.getLevel(this.level))
                .append('[').append(this.tag).append(']')
                .append(' ').append(this.msg).append("\n");
        return sb.toString();
    }

    private String getLevel(int level) {
        String ret = null;
        switch (level) {
            case ELog.LEVEL_V:
                ret = "V";
                break;
            case ELog.LEVEL_D:
                ret = "D";
                break;
            case ELog.LEVEL_I:
                ret = "I";
                break;
            case ELog.LEVEL_W:
                ret = "W";
                break;
            case ELog.LEVEL_E:
                ret = "E";
                break;
            default:
                ret = "D";
        }

        return ret;
    }
}
