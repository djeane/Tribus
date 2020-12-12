package apptribus.com.tribus.activities.send_video;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.camera.CamActivity;
import apptribus.com.tribus.activities.send_video.dagger.DaggerSendVideoComponent;
import apptribus.com.tribus.activities.send_video.dagger.SendVideoModule;
import apptribus.com.tribus.activities.send_video.mvp.SendVideoPresenter;
import apptribus.com.tribus.activities.send_video.mvp.SendVideoView;
import apptribus.com.tribus.application.dagger.TribusApplication;

import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

public class SendVideoActivity extends AppCompatActivity {

    @Inject
    public SendVideoView view;

    @Inject
    public SendVideoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSendVideoComponent.builder()
                .appComponent(TribusApplication.get(this).component())
                .sendVideoModule(new SendVideoModule(this))
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
    public void onBackPressed() {
        presenter.releasePlayer();

        if (view.mCameFrom != null) {
            if (view.mCameFrom.equals("fromCamActivityChatTribu")) {
                backToCam();
            } else if (view.mCameFrom.equals("fromShareActivity")) {
                backToChatTribu();
            }
        } else {
            backToChatActivity();
        }
    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    public void backToChatActivity(){
        finish();
    }

    public void backToCam(){
        if(view.mTribuKey != null) {
            Intent intent = new Intent(this, CamActivity.class);
            intent.putExtra(TRIBU_UNIQUE_NAME, view.mTribuKey);
            intent.putExtra(TOPIC_KEY, view.mTopicKey);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }

    public void backToChatTribu(){
        if(view.mTribuKey != null) {
            Intent intent = new Intent(this, ChatTribuActivity.class);
            intent.putExtra(TRIBU_UNIQUE_NAME, view.mTribuKey);
            intent.putExtra(TOPIC_KEY, view.mTopicKey);
            startActivity(intent);
            finish();
        }
        else {
            finish();
        }
    }

}
