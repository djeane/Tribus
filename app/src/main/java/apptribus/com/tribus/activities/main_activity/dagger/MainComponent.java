package apptribus.com.tribus.activities.main_activity.dagger;

import apptribus.com.tribus.application.dagger.AppComponent;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import dagger.Component;

/**
 * Created by User on 5/25/2017.
 */

@MainScope
@Component(modules = {MainModule.class}, dependencies = AppComponent.class)
public interface MainComponent {

    void inject(MainActivity activity);
}
