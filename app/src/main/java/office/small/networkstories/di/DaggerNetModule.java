package office.small.networkstories.di;

import java.util.List;

import dagger.Module;
import dagger.Provides;
import office.small.networkstories.api.RestAPI;
import office.small.networkstories.model.RetrofitModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static office.small.networkstories.model.IConstants.GITAPIURL;

@Module
public class DaggerNetModule {
    @Provides
    Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(GITAPIURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    Call<List<RetrofitModel>> getCall(Retrofit retrofit) {
        RestAPI restAPI = retrofit.create(RestAPI.class);
        return restAPI.loadUsers();
    }
}
