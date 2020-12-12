package apptribus.com.tribus.activities.show_profile_image.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_profile_image.mvp.ShowProfileImageModel;
import apptribus.com.tribus.activities.show_profile_image.mvp.ShowProfileImagePresenter;
import apptribus.com.tribus.activities.show_profile_image.mvp.ShowProfileImageView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 12/25/2017.
 */

@Module
public class ShowProfileImageModule {

    private final AppCompatActivity activity;

    public ShowProfileImageModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowProfileImageScope
    @Provides
    public ShowProfileImageView provideView(){
        return new ShowProfileImageView(activity);
    }

    @ShowProfileImageScope
    @Provides
    public ShowProfileImagePresenter providePresenter(ShowProfileImageView view, ShowProfileImageModel model){
        return new ShowProfileImagePresenter(view, model);
    }

    @ShowProfileImageScope
    @Provides
    public ShowProfileImageModel provideModel(){
        return new ShowProfileImageModel(activity);
    }


}
