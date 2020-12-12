package apptribus.com.tribus.activities.send_video.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_video.mvp.SendVideoModel;
import apptribus.com.tribus.activities.send_video.mvp.SendVideoPresenter;
import apptribus.com.tribus.activities.send_video.mvp.SendVideoView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/12/2017.
 */

@Module
public class SendVideoModule {

    private final AppCompatActivity activity;

    public SendVideoModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SendVideoScope
    @Provides
    public SendVideoView provideView(){
        return new SendVideoView(activity);
    }

    @SendVideoScope
    @Provides
    public SendVideoPresenter providePresenter(SendVideoView view, SendVideoModel model){
        return new SendVideoPresenter(view, model);
    }

    @SendVideoScope
    @Provides
    public SendVideoModel provideModel(){
        return new SendVideoModel(activity);
    }
}
