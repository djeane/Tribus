package apptribus.com.tribus.activities.show_video.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_video.mvp.ShowVideoModel;
import apptribus.com.tribus.activities.show_video.mvp.ShowVideoPresenter;
import apptribus.com.tribus.activities.show_video.mvp.ShowVideoView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/20/2017.
 */
@Module
public class ShowVideoModule {

    private final AppCompatActivity activity;

    public ShowVideoModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowVideoScope
    @Provides
    public ShowVideoView provideView(){
        return new ShowVideoView(activity);
    }

    @ShowVideoScope
    @Provides
    public ShowVideoPresenter providePresenter(ShowVideoView view, ShowVideoModel model){
        return new ShowVideoPresenter(view, model);
    }

    @ShowVideoScope
    @Provides
    public ShowVideoModel provideModel(){
        return new ShowVideoModel(activity);
    }
}
