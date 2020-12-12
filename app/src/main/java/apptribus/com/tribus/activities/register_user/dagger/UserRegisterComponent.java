package apptribus.com.tribus.activities.register_user.dagger;

import apptribus.com.tribus.application.dagger.AppComponent;
import apptribus.com.tribus.activities.register_user.UserRegisterActivity;
import dagger.Component;

/**
 * Created by User on 5/20/2017.
 */

@UserRegisterScope
@Component(modules = {UserRegisterModule.class}, dependencies = AppComponent.class)
public interface UserRegisterComponent {

    void inject(UserRegisterActivity activity);
}
