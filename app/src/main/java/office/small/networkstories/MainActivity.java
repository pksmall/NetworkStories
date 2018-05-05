package office.small.networkstories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            new DownloadPageTask().execute(request);
        } else {
            Toast.makeText(this, "Network is off. Turn it on.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadPageTask extends AsyncTask<Request, Void, String> {
        private static final String DONEURL = "DOWNONEURL";
        Response response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInfoTextView.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.GONE);
            mInfoTextView.setText(s);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Request... urls) {
            try {
                return downloadOneUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        private String downloadOneUrl(Request sUrl) throws IOException {
            String data = "";


            try {

                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            response = client.newCall(sUrl).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
                thread.join();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code" + response);
                } else {
                    Headers responseHeaders = response.headers();
                    Log.d(DONEURL, "HEADERS");
                    for(int i = 0; i < responseHeaders.size(); i++) {
                        Log.d(DONEURL, "Key: " + responseHeaders.name(i) + " Values: " + responseHeaders.value(i));
                    }
                    data = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return  data;
        }
    }
}
