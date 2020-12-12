package apptribus.com.tribus.activities.send_image_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_image_talker.mvp.SendImageTalkerModel;
import apptribus.com.tribus.activities.send_image_talker.mvp.SendImageTalkerPresenter;
import apptribus.com.tribus.activities.send_image_talker.mvp.SendImageTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 8/16/2017.
 */
@Module
public class SendImageTalkerModule {

    private final AppCompatActivity activity;

    public SendImageTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SendImageTalkerScope
    @Provides
    public SendImageTalkerView provideView(){
        return new SendImageTalkerView(activity);
    }

    @SendImageTalkerScope
    @Provides
    public SendImageTalkerPresenter providePresenter(SendImageTalkerView view, SendImageTalkerModel model){
        return new SendImageTalkerPresenter(view, model);
    }

    @SendImageTalkerScope
    @Provides
    public SendImageTalkerModel provideModel(){
        return new SendImageTalkerModel(activity);
    }
}
