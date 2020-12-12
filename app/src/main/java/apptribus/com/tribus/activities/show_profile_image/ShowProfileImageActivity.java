package apptribus.com.tribus.activities.show_profile_image;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_profile_image.dagger.DaggerShowProfileImageComponent;
import apptribus.com.tribus.activities.show_profile_image.dagger.ShowProfileImageModule;
import apptribus.com.tribus.activities.show_profile_image.mvp.ShowProfileImagePresenter;
import apptribus.com.tribus.activities.show_profile_image.mvp.ShowProfileImageView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class ShowProfileImageActivity extends AppCompatActivity {

    @Inject
    ShowProfileImageView view;

    @Inject
    ShowProfileImagePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerShowProfileImageComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .showProfileImageModule(new ShowProfileImageModule(this))
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
}
