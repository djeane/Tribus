package apptribus.com.tribus.activities.detail_add_talker.dagger;

import apptribus.com.tribus.activities.detail_add_talker.DetailAddTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/28/2017.
 */

@DetailAddTalkerScope
@Component(modules = {DetailAddTalkerModule.class}, dependencies = {AppComponent.class})
public interface DetailAddTalkerComponent {

    void inject (DetailAddTalkerActivity activity);
}
