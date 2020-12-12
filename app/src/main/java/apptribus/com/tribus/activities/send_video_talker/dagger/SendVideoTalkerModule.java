package apptribus.com.tribus.activities.send_video_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_video_talker.mvp.SendVideoTalkerModel;
import apptribus.com.tribus.activities.send_video_talker.mvp.SendVideoTalkerPresenter;
import apptribus.com.tribus.activities.send_video_talker.mvp.SendVideoTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 8/16/2017.
 */
@Module
public class SendVideoTalkerModule {

    private final AppCompatActivity activity;

    public SendVideoTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SendVideoTalkerScope
    @Provides
    public SendVideoTalkerView provideView(){
        return new SendVideoTalkerView(activity);
    }

    @SendVideoTalkerScope
    @Provides
    public SendVideoTalkerPresenter providePresenter(SendVideoTalkerView view, SendVideoTalkerModel model){
        return new SendVideoTalkerPresenter(view, model);
    }

    @SendVideoTalkerScope
    @Provides
    public SendVideoTalkerModel provideModel(){
        return new SendVideoTalkerModel(activity);
    }
}
