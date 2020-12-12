package apptribus.com.tribus.activities.send_video.dagger;

import apptribus.com.tribus.activities.send_video.SendVideoActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/12/2017.
 */
@SendVideoScope
@Component(modules = {SendVideoModule.class}, dependencies = {AppComponent.class})
public interface SendVideoComponent {

    void inject(SendVideoActivity activity);
}
