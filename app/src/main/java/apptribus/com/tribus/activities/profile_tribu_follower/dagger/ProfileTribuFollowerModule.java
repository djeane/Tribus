package apptribus.com.tribus.activities.profile_tribu_follower.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.profile_tribu_follower.mvp.ProfileTribuFollowerModel;
import apptribus.com.tribus.activities.profile_tribu_follower.mvp.ProfileTribuFollowerPresenter;
import apptribus.com.tribus.activities.profile_tribu_follower.mvp.ProfileTribuFollowerView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/3/2017.
 */
@Module
public class ProfileTribuFollowerModule {

    private final AppCompatActivity activity;

    public ProfileTribuFollowerModule(AppCompatActivity activity) {
        this.activity = activity;
    }


    @ProfileTribuFollowerScope
    @Provides
    public ProfileTribuFollowerView provideView(){
        return new ProfileTribuFollowerView(activity);
    }


    @ProfileTribuFollowerScope
    @Provides
    public ProfileTribuFollowerPresenter providePresenter(ProfileTribuFollowerView view, ProfileTribuFollowerModel model){
        return new ProfileTribuFollowerPresenter(view, model);
    }


    @ProfileTribuFollowerScope
    @Provides
    public ProfileTribuFollowerModel provideModel(){
        return new ProfileTribuFollowerModel(activity);
    }
}
