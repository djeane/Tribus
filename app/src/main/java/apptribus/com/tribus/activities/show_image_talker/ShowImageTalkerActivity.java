package apptribus.com.tribus.activities.show_image_talker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image.dagger.ShowImageModule;
import apptribus.com.tribus.activities.show_image_talker.dagger.DaggerShowImageTalkerComponent;
import apptribus.com.tribus.activities.show_image_talker.dagger.ShowImageTalkerModule;
import apptribus.com.tribus.activities.show_image_talker.mvp.ShowImageTalkerPresenter;
import apptribus.com.tribus.activities.show_image_talker.mvp.ShowImageTalkerView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ShowImageTalkerActivity extends AppCompatActivity {


    @Inject
    ShowImageTalkerView view;

    @Inject
    ShowImageTalkerPresenter presenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerShowImageTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showImageTalkerModule(new ShowImageTalkerModule(this))
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
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }
}
