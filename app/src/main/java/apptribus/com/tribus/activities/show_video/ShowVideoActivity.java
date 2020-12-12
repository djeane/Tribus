package apptribus.com.tribus.activities.show_video;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_video.dagger.DaggerShowVideoComponent;
import apptribus.com.tribus.activities.show_video.dagger.ShowVideoModule;
import apptribus.com.tribus.activities.show_video.mvp.ShowVideoPresenter;
import apptribus.com.tribus.activities.show_video.mvp.ShowVideoView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ShowVideoActivity extends AppCompatActivity {

    @Inject
    ShowVideoView view;

    @Inject
    ShowVideoPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerShowVideoComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showVideoModule(new ShowVideoModule(this))
                .build().inject(this);

        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);

    }


    @Override
    protected void onPause() {
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.onRestart();
    }

    @Override
    public void onBackPressed() {
        presenter.releasePlayer();
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
