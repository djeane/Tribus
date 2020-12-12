package apptribus.com.tribus.activities.block_user;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.block_user.dagger.BlockUserModule;
import apptribus.com.tribus.activities.block_user.dagger.DaggerBlockUserComponent;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserPresenter;
import apptribus.com.tribus.activities.block_user.mvp.BlockUserView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class BlockUserActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    BlockUserView view;

    @Inject
    BlockUserPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerBlockUserComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .blockUserModule(new BlockUserModule(this))
                .build().inject(this);

        setContentView(view);

        //CHECK INTERNET CONNECTION
        ShowSnackBarInfoInternet.checkConnection(view);

    }

    @Override
    public void onBackPressed() {
        finish();
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
        presenter.onResume();
        TribusApplication.getInstance().setConnectivityListener(this);
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
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
