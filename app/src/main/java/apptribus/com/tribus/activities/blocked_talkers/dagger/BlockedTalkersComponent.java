package apptribus.com.tribus.activities.blocked_talkers.dagger;

import apptribus.com.tribus.activities.blocked_talkers.BlockedTalkersActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 7/11/2017.
 */
@BlockedTalkersScope
@Component(modules = {BlockedTalkersModule.class}, dependencies = {AppComponent.class})
public interface BlockedTalkersComponent {

    void inject(BlockedTalkersActivity activity);
}
