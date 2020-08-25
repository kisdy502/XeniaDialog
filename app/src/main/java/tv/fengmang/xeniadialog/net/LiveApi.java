package tv.fengmang.xeniadialog.net;


import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.Nullable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import tv.fengmang.xeniadialog.bean.BaseLiveData;
import tv.fengmang.xeniadialog.bean.ChannelProgram;

/**
 * Created by Administrator on 2020/2/12 0012.
 */

public interface LiveApi {

    @GET("/liveApi/api/v2/listClassAndChannel.action")
    Call<BaseLiveData> getBaseData(@Query("hideCateSwitch") String hideCateSwitch, @Query("version") String version);


    @GET("/liveApi/api/v2/listClassAndChannel.action")
    Observable<BaseLiveData> getBaseData2(@Query("hideCateSwitch") String hideCateSwitch, @Query("version") String version);


    @GET("/liveApi/api/v2/listScheduleByClass.action")
    Observable<ChannelProgram> getProgramList(@QueryMap Map<String, String> queryMap);

}
