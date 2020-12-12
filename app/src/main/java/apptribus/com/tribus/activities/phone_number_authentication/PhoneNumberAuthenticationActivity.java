package apptribus.com.tribus.activities.phone_number_authentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.phone_number_authentication.dagger.DaggerPhoneNumberAuthenticationComponent;
import apptribus.com.tribus.activities.phone_number_authentication.dagger.PhoneNumberAuthenticationModule;
import apptribus.com.tribus.activities.phone_number_authentication.mvp.PhoneNumberAuthenticationPresenter;
import apptribus.com.tribus.activities.phone_number_authentication.mvp.PhoneNumberAuthenticationView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class PhoneNumberAuthenticationActivity extends AppCompatActivity {

    @Inject
    PhoneNumberAuthenticationView view;

    @Inject
    PhoneNumberAuthenticationPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerPhoneNumberAuthenticationComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .phoneNumberAuthenticationModule(new PhoneNumberAuthenticationModule(this))
                .build().inject(this);

        setContentView(view);

    }


    @Override
    protected void onStart() {
        presenter.onStart();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}

