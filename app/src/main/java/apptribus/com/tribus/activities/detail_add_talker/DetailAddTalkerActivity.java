package apptribus.com.tribus.activities.detail_add_talker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import apptribus.com.tribus.activities.detail_add_talker.dagger.DaggerDetailAddTalkerComponent;
import apptribus.com.tribus.activities.detail_add_talker.dagger.DetailAddTalkerModule;
import apptribus.com.tribus.activities.detail_add_talker.mvp.DetailAddTalkerPresenter;
import apptribus.com.tribus.activities.detail_add_talker.mvp.DetailAddTalkerView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class DetailAddTalkerActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    DetailAddTalkerView view;

    @Inject
    DetailAddTalkerPresenter presenter;

    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerDetailAddTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .detailAddTalkerModule(new DetailAddTalkerModule(this))
                .build().inject(this);

        setContentView(view);

        //CHECK INTERNET CONNECTION
        v = findViewById(android.R.id.content);
        ShowSnackBarInfoInternet.checkConnection(v);

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
