package apptribus.com.tribus.activities.detail_talker.dagger;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/30/2017.
 */

@DetailTalkerScope
@Component(modules = {DetailTalkerModule.class}, dependencies = {AppComponent.class})
public interface DetailTalkerComponent {

    void inject(DetailTalkerActivity activity);
}
