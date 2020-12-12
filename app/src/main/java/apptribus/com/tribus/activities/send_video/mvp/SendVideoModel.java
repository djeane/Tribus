package apptribus.com.tribus.activities.send_video.mvp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.camera.CamActivity;
import apptribus.com.tribus.activities.send_video.repository.SendVideoAPI;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/12/2017.
 */

public class SendVideoModel {

    public final AppCompatActivity activity;

    public SendVideoModel(AppCompatActivity activity) {
        this.activity = activity;
    }


    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return SendVideoAPI.getTribu(uniqueName);
    }

    //Get the current user
    public Observable<User> getUser() {
        return SendVideoAPI.getUser();
    }

    //VIDEO MESSAGE
    //Method that call API to send video to Firebase
    public void uploadVideoToFirebase(User user, Uri fileName, String description, String duration, int videoSize, String mTopicKey,
                                      String mTribuKey) {
        SendVideoAPI.uploadVideoToFirebase(user, fileName, description, duration, activity, videoSize, mTopicKey, mTribuKey);
    }

    public void backToChatActivity(){
        activity.finish();
    }

    public void backToCam(String mTribusKey, String mTopicKey){
        Intent intent = new Intent(activity, CamActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        intent.putExtra(TOPIC_KEY, mTopicKey);
        activity.startActivity(intent);
        activity.finish();
    }

    public void backToChatTribu(String mTribusKey, String mTopicKey){
        Intent intent = new Intent(activity, ChatTribuActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        intent.putExtra(TOPIC_KEY, mTopicKey);
        activity.startActivity(intent);
        activity.finish();
    }

}
