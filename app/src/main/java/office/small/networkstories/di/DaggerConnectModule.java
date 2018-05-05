package office.small.networkstories.di;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import dagger.Module;
import dagger.Provides;

@Module
public class DaggerConnectModule {
    @Provides
    ConnectivityManager getConnectivityManager(Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    NetworkInfo getNetworkInfo(ConnectivityManager connectivityManager) {
        return  connectivityManager.getActiveNetworkInfo();
    }
}
