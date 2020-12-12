package apptribus.com.tribus.activities.show_profile_image.dagger;

import apptribus.com.tribus.activities.show_profile_image.ShowProfileImageActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 12/25/2017.
 */

@ShowProfileImageScope
@Component(modules = {ShowProfileImageModule.class}, dependencies = {AppComponent.class})
public interface ShowProfileImageComponent {

    void inject(ShowProfileImageActivity activity);
}
