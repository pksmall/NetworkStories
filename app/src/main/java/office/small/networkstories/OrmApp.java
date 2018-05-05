package office.small.networkstories;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import office.small.networkstories.di.AppComponent;
import office.small.networkstories.di.AppModule;
import office.small.networkstories.di.DaggerAppComponent;
import office.small.networkstories.di.DaggerConnectModule;
import office.small.networkstories.di.DaggerNetModule;

public class OrmApp extends Application {
    private static AppComponent component;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration= new RealmConfiguration.Builder()
                .name("NetworkStroies")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(configuration);
        component = buildComponent();
    }

    private AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .daggerNetModule(new DaggerNetModule())
                .daggerConnectModule(new DaggerConnectModule())
                .build();
    }

    public static AppComponent getComponent() {
        return component;
    }
}
