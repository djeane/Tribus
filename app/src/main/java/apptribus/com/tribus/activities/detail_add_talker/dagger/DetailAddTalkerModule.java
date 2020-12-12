package apptribus.com.tribus.activities.detail_add_talker.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.detail_add_talker.mvp.DetailAddTalkerModel;
import apptribus.com.tribus.activities.detail_add_talker.mvp.DetailAddTalkerPresenter;
import apptribus.com.tribus.activities.detail_add_talker.mvp.DetailAddTalkerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/28/2017.
 */

@Module
public class DetailAddTalkerModule {

    private final AppCompatActivity activity;

    public DetailAddTalkerModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @DetailAddTalkerScope
    @Provides
    public DetailAddTalkerView provideView(){
        return new DetailAddTalkerView(activity);
    }


    @DetailAddTalkerScope
    @Provides
    public DetailAddTalkerPresenter providePresenter(DetailAddTalkerView view, DetailAddTalkerModel model){
        return new DetailAddTalkerPresenter(view, model);
    }


    @DetailAddTalkerScope
    @Provides
    public DetailAddTalkerModel provideModel(){
        return new DetailAddTalkerModel(activity);
    }
}
