package apptribus.com.tribus.activities.send_video_talker.dagger;

import apptribus.com.tribus.activities.send_video_talker.SendVideoTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 8/16/2017.
 */


@SendVideoTalkerScope
@Component(modules = {SendVideoTalkerModule.class}, dependencies = {AppComponent.class})
public interface SendVideoTalkerComponent {

    void inject(SendVideoTalkerActivity activity);
}
