package apptribus.com.tribus.activities.check_username.dagger;

import apptribus.com.tribus.application.dagger.AppComponent;
import apptribus.com.tribus.activities.check_username.NewCheckUsernameActivity;
import dagger.Component;

/**
 * Created by User on 5/14/2017.
 */

@CheckUsernameScope
@Component(modules = {CheckUsernameModule.class}, dependencies = AppComponent.class)
public interface CheckUsernameComponent {

    void inject(NewCheckUsernameActivity activity);


}
