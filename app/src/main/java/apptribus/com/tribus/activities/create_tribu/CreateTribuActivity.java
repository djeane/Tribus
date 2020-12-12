package apptribus.com.tribus.activities.create_tribu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import javax.inject.Inject;

import apptribus.com.tribus.activities.create_tribu.dagger.CreateTribuModule;
import apptribus.com.tribus.activities.create_tribu.dagger.DaggerCreateTribuComponent;
import apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuPresenter;
import apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuView;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.util.ConnectivityReceiver;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

import static apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.create_tribu.mvp.CreateTribuModel.REQUEST_EXTERNAL_STORAGE;

public class CreateTribuActivity extends AppCompatActivity
        implements ConnectivityReceiver.ConnectivityReceiverListener, IPickResult {

    @Inject
    CreateTribuView view;

    @Inject
    CreateTribuPresenter presenter;

    private View v;


    public static void start(Context context){
        Intent intent = new Intent(context, CreateTribuActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerCreateTribuComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .createTribuModule(new CreateTribuModule(this))
                .build().inject(this);

        setContentView(view);

        //CHECK INTERNET CONNECTION
        v = findViewById(android.R.id.content);
        ShowSnackBarInfoInternet.checkConnection(v);

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);

    }

    @Override
    public void onBackPressed() {
        finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){

            Uri selectedImage = data.getData();
            view.setImageTribu(selectedImage);
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
        ShowSnackBarInfoInternet.showSnack(isConnected, v);
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

            view.setImageTribu(pickResult.getUri());
            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
