package apptribus.com.tribus.activities.chat_user.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_user.mvp.ChatUserModel;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserPresenter;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/27/2017.
 */

@Module
public class ChatUserModule {

    private final AppCompatActivity activity;

    public ChatUserModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @ChatUserScope
    @Provides
    public ChatUserView provideView(){
        return new ChatUserView(activity);
    }


    @ChatUserScope
    @Provides
    public ChatUserPresenter providePresenter(ChatUserView view, ChatUserModel model){
        return new ChatUserPresenter(view, model);
    }


    @ChatUserScope
    @Provides
    public ChatUserModel provideModel(){
        return new ChatUserModel(activity);
    }
}
