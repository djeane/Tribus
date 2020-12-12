package apptribus.com.tribus.activities.profile_tribu_user.dagger;

import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/3/2017.
 */
@ProfileTribuUserScope
@Component(modules = {ProfileTribuUserModule.class}, dependencies = {AppComponent.class})
public interface ProfileTribuUserComponent {

    void inject(ProfileTribuUserActivity activity);
}
