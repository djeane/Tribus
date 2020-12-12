package apptribus.com.tribus.activities.conversation_topics.dagger;

import apptribus.com.tribus.activities.conversation_topics.ConversationTopicsActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 12/25/2017.
 */
@ConversationTopicsScope
@Component(modules = {ConversationTopicsModule.class}, dependencies = {AppComponent.class})
public interface ConversationTopicsComponent {

    void inject(ConversationTopicsActivity activity);
}
