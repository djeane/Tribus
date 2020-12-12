package apptribus.com.tribus.activities.user_profile.dagger;

import apptribus.com.tribus.activities.user_profile.UserProfileActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/30/2017.
 */

@UserProfileScope
@Component(modules = {UserProfileModule.class}, dependencies = {AppComponent.class})
public interface UserProfileComponent {

    void inject(UserProfileActivity activity);
}
