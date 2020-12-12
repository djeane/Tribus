package apptribus.com.tribus.activities.show_image.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_image.mvp.ShowImageModel;
import apptribus.com.tribus.activities.show_image.mvp.ShowImagePresenter;
import apptribus.com.tribus.activities.show_image.mvp.ShowImageView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/20/2017.
 */
@Module
public class ShowImageModule {

    private final AppCompatActivity activity;

    public ShowImageModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ShowImageScope
    @Provides
    public ShowImageView provideView(){
        return new ShowImageView(activity);
    }

    @ShowImageScope
    @Provides
    public ShowImagePresenter providePresenter(ShowImageView view, ShowImageModel model){
        return new ShowImagePresenter(view, model);
    }

    @ShowImageScope
    @Provides
    public ShowImageModel provideModel(){
        return new ShowImageModel(activity);
    }
}
