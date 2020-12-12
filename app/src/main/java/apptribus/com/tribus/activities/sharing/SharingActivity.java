package apptribus.com.tribus.activities.sharing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.vansuita.pickimage.listeners.IPickResult;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.sharing.dagger.DaggerSharingComponent;
import apptribus.com.tribus.activities.sharing.dagger.SharingModule;
import apptribus.com.tribus.activities.sharing.mvp.SharingPresenter;
import apptribus.com.tribus.activities.sharing.mvp.SharingView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class SharingActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    SharingView view;

    @Inject
    SharingPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSharingComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .sharingModule(new SharingModule(this))
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
