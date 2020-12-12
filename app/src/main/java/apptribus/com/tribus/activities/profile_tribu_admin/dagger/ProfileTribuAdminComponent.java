package apptribus.com.tribus.activities.profile_tribu_admin.dagger;

import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/3/2017.
 */
@ProfileTribuAdminScope
@Component(modules = {ProfileTribuAdminModule.class}, dependencies = {AppComponent.class})
public interface ProfileTribuAdminComponent {

    void inject(ProfileTribuAdminActivity activity);
}
