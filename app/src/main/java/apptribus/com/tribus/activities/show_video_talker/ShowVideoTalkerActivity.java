package apptribus.com.tribus.activities.show_video_talker;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_video_talker.dagger.DaggerShowVideoTalkerComponent;
import apptribus.com.tribus.activities.show_video_talker.dagger.ShowVideoTalkerModule;
import apptribus.com.tribus.activities.show_video_talker.mvp.ShowVideoTalkerPresenter;
import apptribus.com.tribus.activities.show_video_talker.mvp.ShowVideoTalkerView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ShowVideoTalkerActivity extends AppCompatActivity {


    @Inject
    ShowVideoTalkerView view;

    @Inject
    ShowVideoTalkerPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerShowVideoTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showVideoTalkerModule(new ShowVideoTalkerModule(this))
                .build()
                .inject(this);

        setContentView(view);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);

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
    public void onBackPressed() {
        presenter.releasePlayer();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
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
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
