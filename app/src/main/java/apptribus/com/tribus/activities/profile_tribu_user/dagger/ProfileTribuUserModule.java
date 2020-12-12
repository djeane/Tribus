package apptribus.com.tribus.activities.profile_tribu_user.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserModel;
import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserPresenter;
import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/3/2017.
 */
@Module
public class ProfileTribuUserModule {

    private final AppCompatActivity activity;

    public ProfileTribuUserModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ProfileTribuUserScope
    @Provides
    public ProfileTribuUserView provideView(){
        return new ProfileTribuUserView(activity);
    }

    @ProfileTribuUserScope
    @Provides
    public ProfileTribuUserPresenter providePresenter(ProfileTribuUserView view, ProfileTribuUserModel model){
        return new ProfileTribuUserPresenter(view, model);
    }

    @ProfileTribuUserScope
    @Provides
    public ProfileTribuUserModel provideModel(){
        return new ProfileTribuUserModel(activity);
    }
}
