package apptribus.com.tribus.activities.chat_tribu.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuModel;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuPresenter;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/8/2017.
 */

@Module
public class ChatTribuModule {

    private final AppCompatActivity activity;

    public ChatTribuModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @ChatTribuScope
    @Provides
    public ChatTribuView provideView(){
        return new ChatTribuView(activity);
    }


    @ChatTribuScope
    @Provides
    public ChatTribuPresenter providePresenter(ChatTribuView view, ChatTribuModel model){
        return new ChatTribuPresenter(view, model);
    }


    @ChatTribuScope
    @Provides
    public ChatTribuModel provideModel(){
        return new ChatTribuModel(activity);
    }
}
