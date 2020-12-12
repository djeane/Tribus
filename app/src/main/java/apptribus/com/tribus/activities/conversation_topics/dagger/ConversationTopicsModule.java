package apptribus.com.tribus.activities.conversation_topics.dagger;

import android.support.v7.app.AppCompatActivity;

import javax.inject.Scope;

import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsModel;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsPresenter;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 12/25/2017.
 */
@Module
public class ConversationTopicsModule {

    private final AppCompatActivity activity;

    public ConversationTopicsModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ConversationTopicsScope
    @Provides
    public ConversationTopicsView provideView(){
        return new ConversationTopicsView(activity);
    }

    @ConversationTopicsScope
    @Provides
    public ConversationTopicsPresenter providePresenter(ConversationTopicsView view, ConversationTopicsModel model){
        return new ConversationTopicsPresenter(view, model);
    }

    @ConversationTopicsScope
    @Provides
    public ConversationTopicsModel provideModel(){
        return new ConversationTopicsModel(activity);
    }
}
