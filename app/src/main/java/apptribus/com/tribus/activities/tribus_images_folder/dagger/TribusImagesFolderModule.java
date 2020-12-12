package apptribus.com.tribus.activities.tribus_images_folder.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.tribus_images_folder.mvp.TribusImagesFolderModel;
import apptribus.com.tribus.activities.tribus_images_folder.mvp.TribusImagesFolderPresenter;
import apptribus.com.tribus.activities.tribus_images_folder.mvp.TribusImagesFolderView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 9/23/2017.
 */

@Module
public class TribusImagesFolderModule {

    private final AppCompatActivity activity;

    public TribusImagesFolderModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @TribusImagesFolderScope
    @Provides
    public TribusImagesFolderView provideView(){
        return new TribusImagesFolderView(activity);
    }

    @TribusImagesFolderScope
    @Provides
    public TribusImagesFolderPresenter providePresenter(TribusImagesFolderView view, TribusImagesFolderModel model){
        return new TribusImagesFolderPresenter(view, model);
    }

    @TribusImagesFolderScope
    @Provides
    public TribusImagesFolderModel provideModel(){
        return new TribusImagesFolderModel(activity);
    }
}
