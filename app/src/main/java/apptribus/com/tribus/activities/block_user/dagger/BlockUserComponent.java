package apptribus.com.tribus.activities.block_user.dagger;

import apptribus.com.tribus.activities.block_user.BlockUserActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/4/2017.
 */
@BlockUserScope
@Component(modules = {BlockUserModule.class}, dependencies = {AppComponent.class})
public interface BlockUserComponent {

    void inject(BlockUserActivity activity);
}
