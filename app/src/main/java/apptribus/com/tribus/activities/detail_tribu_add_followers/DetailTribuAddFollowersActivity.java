package apptribus.com.tribus.activities.detail_tribu_add_followers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.detail_tribu_add_followers.dagger.DaggerDetailTribuAddFollowersComponent;
import apptribus.com.tribus.activities.detail_tribu_add_followers.dagger.DetailTribuAddFollowersModule;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersPresenter;
import apptribus.com.tribus.activities.detail_tribu_add_followers.mvp.DetailTribuAddFollowersView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class DetailTribuAddFollowersActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {


    @Inject
    DetailTribuAddFollowersView view;

    @Inject
    DetailTribuAddFollowersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerDetailTribuAddFollowersComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .detailTribuAddFollowersModule(new DetailTribuAddFollowersModule(this))
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
        //register connection status listener
        TribusApplication.getInstance().setConnectivityListener(this);
        presenter.onResume();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (view.fromNotification == null){
            finish();
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    //CHECK INTERNET CONNECTION
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
