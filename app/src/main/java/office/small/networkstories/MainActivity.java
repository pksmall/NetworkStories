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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;
    OkHttpClient client;
    HttpUrl.Builder urlBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);
        Button btnLoad = findViewById(R.id.btnLoad);
        client = new OkHttpClient();
        btnLoad.setOnClickListener((v) -> onClick());
    }

    private void onClick() {
        urlBuilder = HttpUrl.parse("https://api.github.com/users").newBuilder();
        if (!editText.getText().toString().isEmpty()) {
            urlBuilder.addEncodedPathSegment(editText.getText().toString()); // one user
        }

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().url(url).build();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            try {
                downloadOneUrl(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Network is off. Turn it on.", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadOneUrl(Request sUrl) throws IOException {
        progressBar.setVisibility(View.VISIBLE);

        client.newCall(sUrl).enqueue(new Callback() {
            public static final String DONEURL = "DWNONEURL";

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code" + response);
                } else {
                    Headers responseHeaders = response.headers();
                    Log.d(DONEURL, "HEADERS");
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        Log.d(DONEURL, "Key: " + responseHeaders.name(i) + " Values: " + responseHeaders.value(i));
                    }
                    final String responseData = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mInfoTextView.setText(responseData);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
