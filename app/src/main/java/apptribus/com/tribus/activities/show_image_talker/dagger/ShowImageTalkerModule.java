package apptribus.com.tribus.activities.show_image_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_image_talker.mvp.ShowImageTalkerModel;
import apptribus.com.tribus.activities.show_image_talker.mvp.ShowImageTalkerPresenter;
import apptribus.com.tribus.activities.show_image_talker.mvp.ShowImageTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 8/16/2017.
 */
@Module
public class ShowImageTalkerModule {

    private final AppCompatActivity activity;

    public ShowImageTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowImageTalkerScope
    @Provides
    public ShowImageTalkerView provideView(){
        return new ShowImageTalkerView(activity);
    }

    @ShowImageTalkerScope
    @Provides
    public ShowImageTalkerPresenter providePresenter(ShowImageTalkerView view, ShowImageTalkerModel model){
        return new ShowImageTalkerPresenter(view, model);
    }

    @ShowImageTalkerScope
    @Provides
    public ShowImageTalkerModel provideModel(){
        return new ShowImageTalkerModel(activity);
    }
}

