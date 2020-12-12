package apptribus.com.tribus.activities.phone_number_authentication.dagger;

import apptribus.com.tribus.activities.phone_number_authentication.PhoneNumberAuthenticationActivity;
import apptribus.com.tribus.application.dagger.AppComponent;
import dagger.Component;

/**
 * Created by User on 9/10/2017.
 */
@PhoneNumberAuthenticationScope
@Component(modules = {PhoneNumberAuthenticationModule.class}, dependencies = {AppComponent.class})
public interface PhoneNumberAuthenticationComponent {

    void inject(PhoneNumberAuthenticationActivity activity);
}
