package apptribus.com.tribus.activities.chat_tribu.dagger;

import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 6/8/2017.
 */

@ChatTribuScope
@Component(modules = {ChatTribuModule.class}, dependencies = AppComponent.class)
public interface ChatTribuComponent {

    void inject(ChatTribuActivity activity);
}
