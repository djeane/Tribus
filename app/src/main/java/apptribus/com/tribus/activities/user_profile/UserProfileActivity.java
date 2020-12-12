package apptribus.com.tribus.activities.user_profile;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.user_profile.dagger.DaggerUserProfileComponent;
import apptribus.com.tribus.activities.user_profile.dagger.UserProfileModule;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfilePresenter;
import apptribus.com.tribus.activities.user_profile.mvp.UserProfileView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

import static apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel.REQUEST_EXTERNAL_STORAGE;
import static apptribus.com.tribus.activities.user_profile.mvp.UserProfileModel.CAMERA_REQUEST;

public class UserProfileActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener, IPickResult {

    @Inject
    UserProfileView view;

    @Inject
    UserProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerUserProfileComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .userProfileModule(new UserProfileModule(this))
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);
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

    @Override
    public void onPickResult(PickResult pickResult) {

        if (pickResult.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            //getImageView().setImageBitmap(r.getBitmap());

            presenter.setImageUser(pickResult.getUri());;
            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
