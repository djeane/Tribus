package apptribus.com.tribus.activities.new_register_user.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserModel;
import apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserPresenter;
import apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 11/19/2017.
 */
@Module
public class RegisterUserModule {

    private final AppCompatActivity activity;

    public RegisterUserModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @RegisterUserScope
    @Provides
    public RegisterUserView provideView(){
        return new RegisterUserView(activity);
    }

    @RegisterUserScope
    @Provides
    public RegisterUserPresenter providePresenter(RegisterUserView view, RegisterUserModel model){
        return new RegisterUserPresenter(view, model);
    }

    @RegisterUserScope
    @Provides
    public RegisterUserModel provideModel(){
        return new RegisterUserModel(activity);
    }
}
