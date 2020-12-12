package apptribus.com.tribus.activities.show_video_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_video_talker.mvp.ShowVideoTalkerModel;
import apptribus.com.tribus.activities.show_video_talker.mvp.ShowVideoTalkerPresenter;
import apptribus.com.tribus.activities.show_video_talker.mvp.ShowVideoTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 8/16/2017.
 */
@Module
public class ShowVideoTalkerModule {

    private final AppCompatActivity activity;

    public ShowVideoTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowVideoTalkerScope
    @Provides
    public ShowVideoTalkerView provideView(){
        return new ShowVideoTalkerView(activity);
    }

    @ShowVideoTalkerScope
    @Provides
    public ShowVideoTalkerPresenter providePresenter(ShowVideoTalkerView view, ShowVideoTalkerModel model){
        return new ShowVideoTalkerPresenter(view, model);
    }

    @ShowVideoTalkerScope
    @Provides
    public ShowVideoTalkerModel provideModel(){
        return new ShowVideoTalkerModel(activity);
    }
}
