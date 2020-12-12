package apptribus.com.tribus.activities.show_image;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image.dagger.DaggerShowImageComponent;
import apptribus.com.tribus.activities.show_image.dagger.ShowImageModule;
import apptribus.com.tribus.activities.show_image.mvp.ShowImagePresenter;
import apptribus.com.tribus.activities.show_image.mvp.ShowImageView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ShowImageActivity extends AppCompatActivity {

    @Inject
    ShowImageView view;

    @Inject
    ShowImagePresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerShowImageComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showImageModule(new ShowImageModule(this))
                .build().inject(this);

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);

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
}
