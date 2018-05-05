package office.small.networkstories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.realm.Realm;
import io.realm.RealmResults;
import office.small.networkstories.api.RestAPI;
import office.small.networkstories.api.RestAPIUser;
import office.small.networkstories.di.AppComponent;
import office.small.networkstories.model.RealmModel;
import office.small.networkstories.model.RetrofitModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static office.small.networkstories.model.IConstants.DONEURL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;

    Button btnSaveAllRealm;
    Button btnSelectAllRealm;
    Button btnDeleteAllRealm;

    RestAPI restAPI;
    RestAPIUser restAPIUser;
    DisposableSingleObserver<Bundle> dso;
    private Realm realm;

    List<RetrofitModel> modelList = new ArrayList<>();
    AppComponent appComponent;
    @Inject
    Call<List<RetrofitModel>> call;
    @Inject
    NetworkInfo networkInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppComponent appComponent = OrmApp.getComponent();
        appComponent.injectsToMainActivity(this);

        editText = findViewById(R.id.editText);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(this);

        btnSaveAllRealm = findViewById(R.id.btnSaveAllRealm);
        btnSelectAllRealm = findViewById(R.id.btnSelectAllRealm);
        btnDeleteAllRealm = findViewById(R.id.btnDeleteAllRealm);
        btnSaveAllRealm.setOnClickListener(this);
        btnSelectAllRealm.setOnClickListener(this);
        btnDeleteAllRealm.setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private DisposableSingleObserver<Bundle> CreateObserver() {
        return new DisposableSingleObserver<Bundle>() {
            @Override
            protected void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
                mInfoTextView.setText("");
            }

            @Override
            public void onSuccess(Bundle bundle) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.append("Total = " + bundle.getInt("count") +
                    "\nmsek = " + bundle.getLong("msek"));
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                mInfoTextView.setText("DB error: " + e.getMessage());
            }
        };
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveAllRealm:
                Single<Bundle> singleSaveAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> e) throws Exception {
                        try {
                            realm = Realm.getDefaultInstance();
                            Date first = new Date();
                            for (RetrofitModel curItem: modelList) {
                                try {
                                    realm.executeTransactionAsync(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            String curLogin = curItem.getLogin();
                                            String curUserID = curItem.getId();
                                            String curAvatarUrl = curItem.getAvatarUrl();

                                            RealmModel realmModel = realm.createObject(RealmModel.class);
                                            realmModel.setUserId(curUserID);
                                            realmModel.setLogin(curLogin);
                                            realmModel.setAvatarUrl(curAvatarUrl);
                                        }
                                    });
                                } catch (Exception ex) {
                                    realm.cancelTransaction();
                                    e.onError(ex);
                                }
                            }
                            Date second = new Date();
                            RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            e.onSuccess(bundle);
                            realm.close();
                        } catch (Exception ex) {
                            e.onError(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSaveAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnSelectAllRealm:
                Single<Bundle> singleSelectAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> e) throws Exception {
                        try {
                            realm = Realm.getDefaultInstance();
                            Date first = new Date();
                            RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            e.onSuccess(bundle);
                            realm.close();
                        } catch (Exception ex) {
                            e.onError(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleSelectAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnDeleteAllRealm:
                Single<Bundle> singleDeleteAllRealm = Single.create(new SingleOnSubscribe<Bundle>() {
                    @Override
                    public void subscribe(@NonNull SingleEmitter<Bundle> e) throws Exception {
                        try {
                            realm = Realm.getDefaultInstance();
                            final RealmResults<RealmModel> tempList = realm.where(RealmModel.class).findAll();
                            Date first = new Date();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    tempList.deleteAllFromRealm();
                                }
                            });
                            Date second = new Date();
                            Bundle bundle = new Bundle();
                            bundle.putInt("count", tempList.size());
                            bundle.putLong("msek", second.getTime() - first.getTime());
                            e.onSuccess(bundle);
                            realm.close();
                        } catch (Exception ex) {
                            e.onError(ex);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                singleDeleteAllRealm.subscribeWith(CreateObserver());
                break;
            case R.id.btnLoad:
                mInfoTextView.setText("");

                /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo(); */

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
                break;

        }
    }

    private void downloadOneUrl(Call<List<RetrofitModel>> call) throws IOException {
        call.enqueue(new Callback<List<RetrofitModel>>() {
            @Override
            public void onResponse(Call<List<RetrofitModel>> call, Response<List<RetrofitModel>> response) {
                if (response.isSuccessful()) {
                    if (response != null) {
                        RetrofitModel curRetrofitModel = null;
                        mInfoTextView.append("\nSize = " + response.body().size() +
                            "\n----------------");
                        for (int i=0; i < response.body().size(); i++) {
                            curRetrofitModel = response.body().get(i);
                            modelList.add(curRetrofitModel);
                            mInfoTextView.append(
                                    "\nLogin = " + curRetrofitModel.getLogin() +
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
