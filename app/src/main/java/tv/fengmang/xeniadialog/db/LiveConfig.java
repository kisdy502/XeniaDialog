package tv.fengmang.xeniadialog.db;

import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = DbLive.class, name = "LiveConfig")
public class LiveConfig extends BaseModel {

    public final static String KEY_BASEDATA_VERSION = "base_data_version";
    @Column
    @PrimaryKey(autoincrement = true)
    int id;
    @Column
    private String key;
    @Column
    private String value;
    @Column
    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
