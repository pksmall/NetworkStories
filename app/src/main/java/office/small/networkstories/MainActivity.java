package office.small.networkstories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import office.small.networkstories.api.RestAPI;
import office.small.networkstories.model.RetrofitModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static final String DONEURL = "DWNONEURL";
    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;
    RestAPI restAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener((v) -> onClick());
    }

    private void onClick() {
        mInfoTextView.setText("");
        Retrofit retrofit = null;

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com/users/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            restAPI = retrofit.create(RestAPI.class);
        } catch (Exception e) {
            mInfoTextView.setText("no retrofit: " + e.getMessage());
            return;
        }

        Call<List<RetrofitModel>> call = restAPI.loadUsers();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadOneUrl(call);
            } catch (IOException e) {
                e.printStackTrace();
                mInfoTextView.setText(e.getMessage());
            }
        } else {
            Toast.makeText(this, "Network is off. Turn it on.", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Call<List<RetrofitModel>> call) throws IOException {
        call.enqueue(new Callback<List<RetrofitModel>>() {
            @Override
            public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        RetrofitModel curRetrofitModel = null;
                        for (int i=0; i < response.body().size(); i++) {
                            curRetrofitModel = response.body().get(i);
                            mInfoTextView.append("\nLogin = " + curRetrofitModel.getLogin() +
                                "\nId = " + curRetrofitModel.getId() +
                                "\nURI = " + curRetrofitModel.getAvatarUrl() +
                                "\n------------");
                        }
                    }
                } else {
                    Log.d(DONEURL, "onResponse errro: " + response.code());
                    mInfoTextView.setText("onResponse error: " + response.code());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RetrofitModel>> call, Throwable t) {
                Log.d(DONEURL, "onFailure: " + t);
                mInfoTextView.setText("onFailure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
