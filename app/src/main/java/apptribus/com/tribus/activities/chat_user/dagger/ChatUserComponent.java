package apptribus.com.tribus.activities.chat_user.dagger;

import apptribus.com.tribus.activities.chat_tribu.dagger.ChatTribuScope;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/27/2017.
 */
@ChatUserScope
@Component(modules = {ChatUserModule.class}, dependencies = {AppComponent.class})
public interface ChatUserComponent {

    void inject(ChatUserActivity activity);
}
