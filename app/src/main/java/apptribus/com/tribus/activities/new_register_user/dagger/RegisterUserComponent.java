package apptribus.com.tribus.activities.new_register_user.dagger;

import apptribus.com.tribus.activities.new_register_user.RegisterUserActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 11/19/2017.
 */

@RegisterUserScope
@Component(modules = {RegisterUserModule.class}, dependencies = {AppComponent.class})
public interface RegisterUserComponent {

    void inject(RegisterUserActivity activity);
}
