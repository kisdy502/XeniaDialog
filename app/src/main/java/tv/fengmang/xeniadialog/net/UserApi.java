package tv.fengmang.xeniadialog.net;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tv.fengmang.xeniadialog.bean.BaseJson;

/**
 * Created by Administrator on 2020/2/12 0012.
 */

public interface UserApi {

    @GET("/liveUser/client/loopUserLogin")
    Observable<BaseJson> getBaseData(@Query("deviceId") String deviceId);
}
