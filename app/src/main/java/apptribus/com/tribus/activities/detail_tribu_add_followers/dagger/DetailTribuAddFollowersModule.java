package apptribus.com.tribus.activities.detail_tribu_add_followers.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersModel;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersPresenter;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/18/2017.
 */

@Module
public class DetailTribuAddFollowersModule {

    private final AppCompatActivity activity;

    public DetailTribuAddFollowersModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @DetailTribuAddFollowersScope
    @Provides
    public DetailTribuAddFollowersView provideView(){
        return new DetailTribuAddFollowersView(activity);
    }


    @DetailTribuAddFollowersScope
    @Provides
    public DetailTribuAddFollowersPresenter providePresenter(DetailTribuAddFollowersView view, DetailTribuAddFollowersModel model){
        return new DetailTribuAddFollowersPresenter(view, model);
    }


    @DetailTribuAddFollowersScope
    @Provides
    public DetailTribuAddFollowersModel provideModel(){
        return new DetailTribuAddFollowersModel(activity);
    }
}
