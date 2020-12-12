package apptribus.com.tribus.activities.detail_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerModel;
import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerPresenter;
import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/30/2017.
 */

@Module
public class DetailTalkerModule {

    private final AppCompatActivity activity;

    public DetailTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @DetailTalkerScope
    @Provides
    public DetailTalkerView provideView(){
        return new DetailTalkerView(activity);
    }

    @DetailTalkerScope
    @Provides
    public DetailTalkerPresenter providePresenter(DetailTalkerView view, DetailTalkerModel model){
        return new DetailTalkerPresenter(view, model);
    }

    @DetailTalkerScope
    @Provides
    public DetailTalkerModel provideModel(){
        return new DetailTalkerModel(activity);
    }
}
