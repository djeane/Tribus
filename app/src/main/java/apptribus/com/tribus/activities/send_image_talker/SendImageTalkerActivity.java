package apptribus.com.tribus.activities.send_image_talker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.chat_user.camera.CamTalkerActivity;
import apptribus.com.tribus.activities.send_image_talker.dagger.DaggerSendImageTalkerComponent;
import apptribus.com.tribus.activities.send_image_talker.dagger.SendImageTalkerModule;
import apptribus.com.tribus.activities.send_image_talker.mvp.SendImageTalkerPresenter;
import apptribus.com.tribus.activities.send_image_talker.mvp.SendImageTalkerView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

public class SendImageTalkerActivity extends AppCompatActivity {


    @Inject
    SendImageTalkerView view;

    @Inject
    SendImageTalkerPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerSendImageTalkerComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .sendImageTalkerModule(new SendImageTalkerModule(this))
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
        if (view.mCameFrom != null) {
            if (view.mCameFrom.equals("fromCamTalkerActivity")) {
                backToCamTalker();
            } else if (view.mCameFrom.equals("fromShareActivity")) {
                backToChatTalker();
            }
        } else {
            backToChatUserActivity();
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    public void backToChatUserActivity(){
        finish();
    }

    public void backToCamTalker(){
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

    public void backToChatTalker(){
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
