package apptribus.com.tribus.activities.check_username;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.activities.check_username.dagger.CheckUsernameModule;
import apptribus.com.tribus.activities.check_username.dagger.DaggerCheckUsernameComponent;
import apptribus.com.tribus.activities.check_username.mvp.CheckUsernamePresenter;
import apptribus.com.tribus.activities.check_username.mvp.CheckUsernameView;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class NewCheckUsernameActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    @Inject
    CheckUsernameView view;

    @Inject
    CheckUsernamePresenter presenter;

    private View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerCheckUsernameComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .checkUsernameModule(new CheckUsernameModule(this))
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
    protected void onResume() {
        super.onResume();
        //register connection status listener
        TribusApplication.getInstance().setConnectivityListener(this);
        presenter.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);

    }


    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    //CHECK INTERNET CONNECTION
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, v);
    }


}
