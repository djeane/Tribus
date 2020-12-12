package apptribus.com.tribus.activities.tribus_images_folder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.activities.tribus_images_folder.dagger.DaggerTribusImagesFolderComponent;
import apptribus.com.tribus.activities.tribus_images_folder.dagger.TribusImagesFolderModule;
import apptribus.com.tribus.activities.tribus_images_folder.mvp.TribusImagesFolderPresenter;
import apptribus.com.tribus.activities.tribus_images_folder.mvp.TribusImagesFolderView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

public class TribusImagesFolderActivity extends AppCompatActivity {

    @Inject
    TribusImagesFolderView view;

    @Inject
    TribusImagesFolderPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerTribusImagesFolderComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .tribusImagesFolderModule(new TribusImagesFolderModule(this))
                .build()
                .inject(this);

        setContentView(view);
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

        finish();

        /*if (view.mCameFrom != null) {
            if (view.mCameFrom.equals("fromProfileTribuUser")) {
                backToProfileTribuUserActivity();
            }
            else if (view.mCameFrom.equals("fromProfileTribuAdmin")) {
                backToProfileTribuAdminActivity();
            }
            else if (view.mCameFrom.equals("fromProfileTribuFollower")) {
                backToProfileTribuAdminActivity();
            }
            else {
                backToMainActivity();
            }
        }
        else {
            backToMainActivity();
        }*/

        super.onBackPressed();
    }

    public void backToMainActivity(){
        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
        finish();
    }

    public void backToProfileTribuUserActivity(){
        finish();
    }

    public void backToProfileTribuAdminActivity(){
        finish();
    }

}
