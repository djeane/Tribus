package apptribus.com.tribus.activities.user_profile.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.user_profile.mvp.UserProfileModel;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfilePresenter;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfileView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 6/30/2017.
 */

@Module
public class UserProfileModule {

    private final AppCompatActivity activity;

    public UserProfileModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @UserProfileScope
    @Provides
    public UserProfileView provideView(){
        return new UserProfileView(activity);
    }


    @UserProfileScope
    @Provides
    public UserProfilePresenter providePresenter(UserProfileView view, UserProfileModel model){
        return new UserProfilePresenter(view, model);
    }


    @UserProfileScope
    @Provides
    public UserProfileModel provideModel(){
        return new UserProfileModel(activity);
    }

}
