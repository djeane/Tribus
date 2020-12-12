package apptribus.com.tribus.activities.send_image_talker.dagger;

import apptribus.com.tribus.activities.send_image_talker.SendImageTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 8/16/2017.
 */
@SendImageTalkerScope
@Component(modules = {SendImageTalkerModule.class}, dependencies = {AppComponent.class})
public interface SendImageTalkerComponent {

    void inject(SendImageTalkerActivity activity);
}
