package apptribus.com.tribus.activities.invitation_request_tribu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.invitation_request_tribu.dagger.DaggerInvitationRequestTribuComponent;
import apptribus.com.tribus.activities.invitation_request_tribu.dagger.InvitationRequestTribuModule;
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuPresenter;
import apptribus.com.tribus.activities.invitation_request_tribu.mvp.InvitationRequestTribuView;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

public class InvitationRequestTribuActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    InvitationRequestTribuView view;

    @Inject
    InvitationRequestTribuPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DaggerInvitationRequestTribuComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .invitationRequestTribuModule(new InvitationRequestTribuModule(this))
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
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (view.fromNotification != null) {
            Intent intent = new Intent(view.mContext, MainActivity.class);
            view.mContext.startActivity(intent);
            view.mContext.finish();

        } else {
            view.mContext.finish();
        }
    }

    //CHECK INTERNET CONNECTION
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ShowSnackBarInfoInternet.showSnack(isConnected, view);
    }
}
