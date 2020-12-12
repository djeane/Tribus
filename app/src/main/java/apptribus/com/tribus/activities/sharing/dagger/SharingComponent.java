package apptribus.com.tribus.activities.sharing.dagger;

import apptribus.com.tribus.activities.sharing.SharingActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 12/12/2017.
 */
@SharingScope
@Component(modules = {SharingModule.class}, dependencies = {AppComponent.class})
public interface SharingComponent {

    void inject(SharingActivity activity);
}
