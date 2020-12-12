package apptribus.com.tribus.activities.main_activity.fragment_tribus.dagger;

import apptribus.com.tribus.activities.main_activity.fragment_tribus.TribusFragment;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/7/2017.
 */
@TribusFragmentScope
@Component(modules = {TribusFragmentModule.class}, dependencies = AppComponent.class)
public interface TribusFragmentComponent {

    void inject(TribusFragment fragment);
}
