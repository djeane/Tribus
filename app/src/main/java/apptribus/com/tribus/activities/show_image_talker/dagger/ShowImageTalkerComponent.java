package apptribus.com.tribus.activities.show_image_talker.dagger;

import apptribus.com.tribus.activities.show_image_talker.ShowImageTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 8/16/2017.
 */
@ShowImageTalkerScope
@Component(modules = {ShowImageTalkerModule.class}, dependencies = {AppComponent.class})
public interface ShowImageTalkerComponent {

    void inject(ShowImageTalkerActivity activity);
}
