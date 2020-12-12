package apptribus.com.tribus.activities.sharing.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.sharing.mvp.SharingModel;
import apptribus.com.tribus.activities.sharing.mvp.SharingPresenter;
import apptribus.com.tribus.activities.sharing.mvp.SharingView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 12/12/2017.
 */
@Module
public class SharingModule {

    private final AppCompatActivity activity;

    public SharingModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @SharingScope
    @Provides
    public SharingView provideView(){
        return new SharingView(activity);
    }

    @SharingScope
    @Provides
    public SharingPresenter providePresenter(SharingView view, SharingModel model){
        return new SharingPresenter(view, model);
    }

    @SharingScope
    @Provides
    public SharingModel provideModel(){
        return new SharingModel(activity);
    }
}
