package apptribus.com.tribus.activities.register_user.dagger;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel;
import apptribus.com.tribus.activities.register_user.mvp.UserRegisterPresenter;
import apptribus.com.tribus.activities.register_user.mvp.UserRegisterView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 5/20/2017.
 */

@Module
public class UserRegisterModule {

    private final AppCompatActivity activity;

    public UserRegisterModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @UserRegisterScope
    @Provides
    public UserRegisterView provideView(){
        return new UserRegisterView(activity);
    }

    @UserRegisterScope
    @Provides
    public UserRegisterPresenter providePresenter(UserRegisterView view, UserRegisterModel model){
        return new UserRegisterPresenter(view, model);
    }

    @UserRegisterScope
    @Provides
    public UserRegisterModel provideModel(){
        return new UserRegisterModel(activity);
    }


}
