package office.small.networkstories.api;

import java.util.List;

import office.small.networkstories.model.RetrofitModel;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RestAPI {
    @GET("users")
    Call<List<RetrofitModel>> loadUsers();
}
