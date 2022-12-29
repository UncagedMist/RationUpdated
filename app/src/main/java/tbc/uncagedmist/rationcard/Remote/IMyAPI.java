package tbc.uncagedmist.rationcard.Remote;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import tbc.uncagedmist.rationcard.Model.Item;
import tbc.uncagedmist.rationcard.Model.State;

public interface IMyAPI {

    @GET("getStates.php")
    Observable<List<State>> getStates();

    @FormUrlEncoded
    @POST("getItems.php")
    Observable<List<Item>> getItems(
            @Field("stateId") String stateId
    );

    @FormUrlEncoded
    @POST("getStateByName.php")
    Observable<List<State>> getStateByName(
            @Field("name") String name
    );
}
