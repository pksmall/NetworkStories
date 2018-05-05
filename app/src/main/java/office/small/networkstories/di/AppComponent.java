package office.small.networkstories.di;

import dagger.Component;
import office.small.networkstories.MainActivity;

@Component(modules = {AppModule.class, DaggerConnectModule.class, DaggerNetModule.class})
public interface AppComponent {
    void injectsToMainActivity(MainActivity mainActivity);
}
