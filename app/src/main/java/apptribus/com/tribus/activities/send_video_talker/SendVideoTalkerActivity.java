package apptribus.com.tribus.activities.send_video_talker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.chat_user.camera.CamTalkerActivity;
import apptribus.com.tribus.activities.send_video_talker.dagger.DaggerSendVideoTalkerComponent;
import apptribus.com.tribus.activities.send_video_talker.dagger.SendVideoTalkerModule;
import apptribus.com.tribus.activities.send_video_talker.mvp.SendVideoTalkerPresenter;
import apptribus.com.tribus.activities.send_video_talker.mvp.SendVideoTalkerView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

public class SendVideoTalkerActivity extends AppCompatActivity {

    @Inject
    public SendVideoTalkerView view;

    @Inject
    public SendVideoTalkerPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSendVideoTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .sendVideoTalkerModule(new SendVideoTalkerModule(this))
                .build().inject(this);

        setContentView(view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }


    @Override
    public void onBackPressed() {
        presenter.releasePlayer();

        if (view.mCameFrom != null) {
            if (view.mCameFrom.equals("fromCamTalkerActivity")) {
                backToCamTalker();
            }
            else if (view.mCameFrom.equals("fromShareActivity")) {
                backToChatTalker();
            }

        } else {
            backToChatUserActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
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
    protected void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    private void backToChatUserActivity(){
        finish();
    }

    private void backToCamTalker(){
        if(view.mTalkerKey != null) {
            Intent intent = new Intent(this, CamTalkerActivity.class);
            intent.putExtra(CONTACT_ID, view.mTalkerKey);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }

    private void backToChatTalker(){
        if(view.mTalkerKey != null) {
            Intent intent = new Intent(this, ChatUserActivity.class);
            intent.putExtra(CONTACT_ID, view.mTalkerKey);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }

}
