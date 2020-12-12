package apptribus.com.tribus.activities.profile_tribu_admin.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminModel;
import apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminPresenter;
import apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 7/3/2017.
 */

@Module
public class ProfileTribuAdminModule {

    private final AppCompatActivity activity;

    public ProfileTribuAdminModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ProfileTribuAdminScope
    @Provides
    public ProfileTribuAdminView provideView(){
        return new ProfileTribuAdminView(activity);
    }


    @ProfileTribuAdminScope
    @Provides
    public ProfileTribuAdminPresenter providePresenter(ProfileTribuAdminView view, ProfileTribuAdminModel model){
        return new ProfileTribuAdminPresenter(view, model);
    }


    @ProfileTribuAdminScope
    @Provides
    public ProfileTribuAdminModel provideModel(){
        return new ProfileTribuAdminModel(activity);
    }
}
