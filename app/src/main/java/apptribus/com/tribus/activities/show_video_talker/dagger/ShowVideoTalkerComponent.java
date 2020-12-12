package apptribus.com.tribus.activities.show_video_talker.dagger;

import apptribus.com.tribus.activities.show_video_talker.ShowVideoTalkerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 8/16/2017.
 */
@ShowVideoTalkerScope
@Component(modules = {ShowVideoTalkerModule.class}, dependencies = {AppComponent.class})
public interface ShowVideoTalkerComponent {

    void inject(ShowVideoTalkerActivity activity);
}
