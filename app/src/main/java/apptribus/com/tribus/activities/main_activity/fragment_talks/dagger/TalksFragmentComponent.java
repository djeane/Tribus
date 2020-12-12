package apptribus.com.tribus.activities.main_activity.fragment_talks.dagger;

import apptribus.com.tribus.activities.main_activity.fragment_talks.TalksFragment;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/13/2017.
 */
@TalksFragmentScope
@Component(modules = {TalksFragmentModule.class}, dependencies = AppComponent.class)
public interface TalksFragmentComponent {

    void inject(TalksFragment fragment);
}
