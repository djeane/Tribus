package apptribus.com.tribus.activities.detail_talker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.detail_talker.dagger.DaggerDetailTalkerComponent;
import apptribus.com.tribus.activities.detail_talker.dagger.DetailTalkerModule;
import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerPresenter;
import apptribus.com.tribus.activities.detail_talker.mvp.DetailTalkerView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class DetailTalkerActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener{

    @Inject
    DetailTalkerView view;

    @Inject
    DetailTalkerPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerDetailTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .detailTalkerModule(new DetailTalkerModule(this))
                .build().inject(this);

        setContentView(view);
        //CHECK INTERNET CONNECTION
        ShowSnackBarInfoInternet.checkConnection(view);
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
