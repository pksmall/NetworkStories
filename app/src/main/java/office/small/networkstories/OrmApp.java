package office.small.networkstories;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class OrmApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration= new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
    }
}
