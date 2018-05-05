package office.small.networkstories.api;

import java.util.List;

import office.small.networkstories.model.RetrofitModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestAPIUser {
    @GET("users/{user}")
    Call<List<RetrofitModel>> loadUsers(@Path("user") String user);
}
