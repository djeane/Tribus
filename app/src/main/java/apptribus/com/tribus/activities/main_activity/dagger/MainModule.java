package apptribus.com.tribus.activities.main_activity.dagger;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.main_activity.mpv.MainModel;
import apptribus.com.tribus.activities.main_activity.mpv.MainPresenter;
import apptribus.com.tribus.activities.main_activity.mpv.MainView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/25/2017.
 */

@Module
public class MainModule {

    public final MainActivity activity;

    public MainModule(MainActivity activity) {
        this.activity = activity;
    }


    @MainScope
    @Provides
    public MainView provideView(){
        return new MainView(activity);
    }


    @MainScope
    @Provides
    public MainPresenter providePresenter(MainView view, MainModel model){
        return new MainPresenter(view, model);
    }


    @MainScope
    @Provides
    public MainModel provideModel(){
        return new MainModel(activity);
    }


}
