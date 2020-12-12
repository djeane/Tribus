package apptribus.com.tribus.activities.change_admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import apptribus.com.tribus.activities.change_admin.dagger.ChangeAdminModule;
import apptribus.com.tribus.activities.change_admin.dagger.DaggerChangeAdminComponent;
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminPresenter;
import apptribus.com.tribus.activities.change_admin.mvp.ChangeAdminView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class ChangeAdminActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    ChangeAdminView view;

    @Inject
    ChangeAdminPresenter presenter;

    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerChangeAdminComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .changeAdminModule(new ChangeAdminModule(this))
                .build().inject(this);

        setContentView(view);

        v = findViewById(android.R.id.content);
        ShowSnackBarInfoInternet.checkConnection(v);

    }


    @Override
    public void onBackPressed() {
        //finish();
        presenter.backToChatActivity();
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
