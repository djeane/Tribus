package apptribus.com.tribus.activities.blocked_talkers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.blocked_talkers.dagger.BlockedTalkersModule;
import apptribus.com.tribus.activities.blocked_talkers.dagger.DaggerBlockedTalkersComponent;
import apptribus.com.tribus.activities.blocked_talkers.mvp.BlockedTalkersPresenter;
import apptribus.com.tribus.activities.blocked_talkers.mvp.BlockedTalkersView;
import apptribus.com.tribus.application.dagger.TribusApplication;

public class BlockedTalkersActivity extends AppCompatActivity {

    @Inject
    BlockedTalkersView view;

    @Inject
    BlockedTalkersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerBlockedTalkersComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .blockedTalkersModule(new BlockedTalkersModule(this))
                .build().inject(this);

        setContentView(view);
    }


    @Override
    public void onBackPressed() {
        finish();
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
}
