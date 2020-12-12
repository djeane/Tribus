package apptribus.com.tribus.activities.profile_tribu_follower.dagger;

import apptribus.com.tribus.activities.profile_tribu_follower.ProfileTribuFollowerActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/3/2017.
 */
@ProfileTribuFollowerScope
@Component(modules = {ProfileTribuFollowerModule.class}, dependencies = {AppComponent.class})
public interface ProfileTribuFollowerComponent {

    void inject(ProfileTribuFollowerActivity activity);
}
