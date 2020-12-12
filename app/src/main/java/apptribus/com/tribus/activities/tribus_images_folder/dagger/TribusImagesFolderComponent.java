package apptribus.com.tribus.activities.tribus_images_folder.dagger;

import apptribus.com.tribus.activities.tribus_images_folder.TribusImagesFolderActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 9/23/2017.
 */
@TribusImagesFolderScope
@Component(modules = {TribusImagesFolderModule.class}, dependencies = {AppComponent.class})
public interface TribusImagesFolderComponent {

    void inject(TribusImagesFolderActivity activity);
}
