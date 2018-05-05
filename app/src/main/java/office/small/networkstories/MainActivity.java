package office.small.networkstories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;


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
        String bastUrl = "https://api.github.com/users";
        if (!editText.getText().toString().isEmpty()) {
            bastUrl += "/" + editText.getText();
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadPageTask().execute(bastUrl);
        } else {
            Toast.makeText(this, "Network is off. Turn it on.", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadPageTask extends AsyncTask<String, Void, String> {
        private static final String DONEURL = "DOWNONEURL";

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
        protected String doInBackground(String... urls) {
            try {
                return downloadOneUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        private String downloadOneUrl(String sUrl) throws IOException {
            InputStream inputStream = null;
            String data = "";

            try {
                URL url = new URL(sUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(DONEURL, "REQ METHOD: " + connection.getRequestMethod());
                    Log.d(DONEURL, "RCV MSG: " + connection.getResponseMessage());

                    Map<String, List<String>> myMap = connection.getHeaderFields();
                    Set<String> myFields = myMap.keySet();
                    Log.d(DONEURL, "HEADERS");
                    for(String k: myFields) {
                        Log.d(DONEURL, "Key: " + k + " Values: " + myMap.get(k));
                    }
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read;
                    while((read = inputStream.read()) != 1) {
                        try {
                            bos.write(read);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                            data = new String("Error. Out of memory");
                            break;
                        }
                        if (bos.size() > 100000) {
                            break;
                        }
                    }
                    byte[] result = bos.toByteArray();
                    bos.close();
                    data = new String(result);
                } else {
                    data = connection.getResponseMessage() + ". Error Code: " + responseCode;
                 }
                 connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return  data;
        }
    }
}
