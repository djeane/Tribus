package apptribus.com.tribus.activities.sharing.fragments.sharing_talker.dagger;

import apptribus.com.tribus.activities.sharing.fragments.sharing_talker.SharingTalkerFragment;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 1/15/2018.
 */
@SharingTalkerFragmentScope
@Component(modules = {SharingTalkerFragmentModule.class}, dependencies = {AppComponent.class})
public interface SharingTalkerFragmentComponent {

    void inject(SharingTalkerFragment fragment);


}
