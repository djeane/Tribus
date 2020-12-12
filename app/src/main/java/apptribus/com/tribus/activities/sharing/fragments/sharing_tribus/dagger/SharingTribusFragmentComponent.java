package apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.dagger;

import apptribus.com.tribus.activities.sharing.fragments.sharing_tribus.SharingTribusFragment;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/15/2018.
 */
@SharingTribusFragmentScope
@Component(modules = {SharingTribusFragmentModule.class}, dependencies = {AppComponent.class})
public interface SharingTribusFragmentComponent {

    void inject (SharingTribusFragment fragment);
}
