package apptribus.com.tribus.activities.profile_tribu_follower;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.profile_tribu_follower.dagger.DaggerProfileTribuFollowerComponent;
import apptribus.com.tribus.activities.profile_tribu_follower.dagger.ProfileTribuFollowerModule;
import apptribus.com.tribus.activities.profile_tribu_follower.mvp.ProfileTribuFollowerPresenter;
import apptribus.com.tribus.activities.profile_tribu_follower.mvp.ProfileTribuFollowerView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class ProfileTribuFollowerActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    ProfileTribuFollowerView view;

    @Inject
    ProfileTribuFollowerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProfileTribuFollowerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .profileTribuFollowerModule(new ProfileTribuFollowerModule(this))
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
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
