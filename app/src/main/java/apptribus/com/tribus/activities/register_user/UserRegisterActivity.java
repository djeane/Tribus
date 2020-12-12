package apptribus.com.tribus.activities.register_user;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.check_username.NewCheckUsernameActivity;
import apptribus.com.tribus.application.dagger.TribusApplication;
import apptribus.com.tribus.activities.register_user.dagger.DaggerUserRegisterComponent;
import apptribus.com.tribus.activities.register_user.dagger.UserRegisterModule;
import apptribus.com.tribus.activities.register_user.mvp.UserRegisterPresenter;
import apptribus.com.tribus.activities.register_user.mvp.UserRegisterView;

import static apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel.GALLERY_REQUEST;
import static apptribus.com.tribus.activities.register_user.mvp.UserRegisterModel.REQUEST_EXTERNAL_STORAGE;

public class UserRegisterActivity extends AppCompatActivity {

    @Inject
    UserRegisterView view;

    @Inject
    UserRegisterPresenter presenter;

    public static void start(Context context){
        Intent intent = new Intent(context, UserRegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerUserRegisterComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .userRegisterModule(new UserRegisterModule(this))
                .build().inject(this);

        setContentView(view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }


    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presenter.onResume();
        super.onResume();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, NewCheckUsernameActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null){
            Log.d("Valor: ", "requestCode == GALLERY_REQUEST: " + GALLERY_REQUEST);
            Log.d("Valor: ", "resultCode == RESULT_OK: " + RESULT_OK);
            Log.d("Valor: ", "data: " + data);

            Uri selectedImage = data.getData();
            Log.d("Valor: ", "selectedImage: " + selectedImage);
            view.setImageUser(selectedImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    showMissingPermissionError();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showMissingPermissionError() {
        Toast.makeText(this, R.string.permission_request_image, Toast.LENGTH_SHORT).show();
        finish();
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

}
