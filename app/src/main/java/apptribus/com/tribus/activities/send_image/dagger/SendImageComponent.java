package apptribus.com.tribus.activities.send_image.dagger;

import apptribus.com.tribus.activities.send_image.SendImageActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/12/2017.
 */
@SendImageScope
@Component(modules = {SendImageModule.class}, dependencies = {AppComponent.class})
public interface SendImageComponent {

    void inject(SendImageActivity activity);
}
