package apptribus.com.tribus.activities.phone_number_authentication.dagger;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.phone_number_authentication.mvp.PhoneNumberAuthenticationModel;
import apptribus.com.tribus.activities.phone_number_authentication.mvp.PhoneNumberAuthenticationPresenter;
import apptribus.com.tribus.activities.phone_number_authentication.mvp.PhoneNumberAuthenticationView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by User on 9/10/2017.
 */
@Module
public class PhoneNumberAuthenticationModule {

    private final AppCompatActivity activity;

    public PhoneNumberAuthenticationModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @PhoneNumberAuthenticationScope
    @Provides
    public PhoneNumberAuthenticationView provideView(){
        return new PhoneNumberAuthenticationView(activity);
    }

    @PhoneNumberAuthenticationScope
    @Provides
    public PhoneNumberAuthenticationPresenter providePresenter(PhoneNumberAuthenticationView view, PhoneNumberAuthenticationModel model){
        return new PhoneNumberAuthenticationPresenter(view, model);
    }

    @PhoneNumberAuthenticationScope
    @Provides
    public PhoneNumberAuthenticationModel provideModel(){
        return new PhoneNumberAuthenticationModel(activity);
    }
}
