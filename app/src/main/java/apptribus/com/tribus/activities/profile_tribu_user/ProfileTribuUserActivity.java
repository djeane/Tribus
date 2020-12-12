package apptribus.com.tribus.activities.profile_tribu_user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_user.dagger.DaggerProfileTribuUserComponent;
import apptribus.com.tribus.activities.profile_tribu_user.dagger.ProfileTribuUserModule;
import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserPresenter;
import apptribus.com.tribus.activities.profile_tribu_user.mvp.ProfileTribuUserView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class ProfileTribuUserActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    ProfileTribuUserView view;

    @Inject
    ProfileTribuUserPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProfileTribuUserComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .profileTribuUserModule(new ProfileTribuUserModule(this))
                .build().inject(this);

        setContentView(view);

        //CHECK INTERNET CONNECTION
        ShowSnackBarInfoInternet.checkConnection(view);
    }


    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (view.fromNotification == null) {
            finish();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
