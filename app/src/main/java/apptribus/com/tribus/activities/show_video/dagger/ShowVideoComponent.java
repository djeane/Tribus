package apptribus.com.tribus.activities.show_video.dagger;

import apptribus.com.tribus.activities.show_video.ShowVideoActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/20/2017.
 */
@ShowVideoScope
@Component(modules = {ShowVideoModule.class}, dependencies = {AppComponent.class})
public interface ShowVideoComponent {

    void inject(ShowVideoActivity activity);
}
