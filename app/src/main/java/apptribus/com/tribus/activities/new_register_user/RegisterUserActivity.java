package apptribus.com.tribus.activities.new_register_user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.dialog.PickImageBaseDialog;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import javax.inject.Inject;

import apptribus.com.tribus.activities.new_register_user.dagger.DaggerRegisterUserComponent;
import apptribus.com.tribus.activities.new_register_user.dagger.RegisterUserModule;
import apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserPresenter;
import apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.activities.new_register_user.mvp.RegisterUserModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel.REQUEST_EXTERNAL_STORAGE;

public class RegisterUserActivity extends AppCompatActivity implements IPickResult {

    @Inject
    RegisterUserView view;

    @Inject
    RegisterUserPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerRegisterUserComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .registerUserModule(new RegisterUserModule(this))
                .build().inject(this);

        setContentView(view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    return;
                    //presenter.showDialogToChooseImage();
                    ///presenter.getImage();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            presenter.setImageUser(selectedImage);
        }
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

            presenter.setImageUser(pickResult.getUri());
            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
