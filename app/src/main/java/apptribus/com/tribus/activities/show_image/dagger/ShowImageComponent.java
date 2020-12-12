package apptribus.com.tribus.activities.show_image.dagger;

import apptribus.com.tribus.activities.show_image.ShowImageActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/20/2017.
 */
@ShowImageScope
@Component(modules = {ShowImageModule.class}, dependencies = {AppComponent.class})
public interface ShowImageComponent {

    void inject(ShowImageActivity activity);

}
