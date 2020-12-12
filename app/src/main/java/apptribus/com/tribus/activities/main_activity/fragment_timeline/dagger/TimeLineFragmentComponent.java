package apptribus.com.tribus.activities.main_activity.fragment_timeline.dagger;

import apptribus.com.tribus.activities.main_activity.fragment_timeline.TimeLineFragment;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 5/27/2017.
 */

@TimeLineFragmentScope
@Component(modules = {TimeLineFragmentModule.class}, dependencies = AppComponent.class)
public interface TimeLineFragmentComponent {

    void inject(TimeLineFragment fragment);
}
