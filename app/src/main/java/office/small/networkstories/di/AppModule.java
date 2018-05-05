package office.small.networkstories.di;

import android.content.Context;
import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private final Context appContext;

    public AppModule(@NonNull Context context) {
        appContext = context;
    }
    @Provides
    Context provideContext() {
        return appContext;
    }
}
