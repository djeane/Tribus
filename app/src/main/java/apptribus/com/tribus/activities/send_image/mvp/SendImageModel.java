package apptribus.com.tribus.activities.send_image.mvp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.camera.CamActivity;
import apptribus.com.tribus.activities.send_image.repository.SendImageAPI;
import apptribus.com.tribus.activities.send_video.repository.SendVideoAPI;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/12/2017.
 */

public class SendImageModel {

    private final AppCompatActivity activity;

    public SendImageModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return SendImageAPI.getTribu(uniqueName);
    }

    //Get the current user
    public Observable<User> getUser() {
        return SendImageAPI.getUser();
    }

    //VIDEO MESSAGE
    //Method that call API to send image to Firebase
    public void uploadImageToFirebase(User user, Uri fileName, String description, int videoSize, String mTopicKey, String mTribuKey) {
        SendImageAPI.uploadImageToFirebase(user, fileName, description, activity, videoSize, mTopicKey, mTribuKey);
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
