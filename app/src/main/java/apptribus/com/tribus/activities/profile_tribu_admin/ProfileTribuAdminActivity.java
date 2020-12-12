package apptribus.com.tribus.activities.profile_tribu_admin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import javax.inject.Inject;

import apptribus.com.tribus.activities.profile_tribu_admin.dagger.DaggerProfileTribuAdminComponent;
import apptribus.com.tribus.activities.profile_tribu_admin.dagger.ProfileTribuAdminModule;
import apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminPresenter;
import apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

import static apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.profile_tribu_admin.mvp.ProfileTribuAdminModel.REQUEST_EXTERNAL_STORAGE;


public class ProfileTribuAdminActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener, IPickResult {

    @Inject
    ProfileTribuAdminView view;

    @Inject
    ProfileTribuAdminPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerProfileTribuAdminComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .profileTribuAdminModule(new ProfileTribuAdminModule(this))
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

    //GET IMAGE FROM GALLERY
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            presenter.setImageTribu(selectedImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    presenter.getImage();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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

            presenter.setImageTribu(pickResult.getUri());;
            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
