package apptribus.com.tribus.activities.send_video_talker.mvp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.chat_user.camera.CamTalkerActivity;
import apptribus.com.tribus.activities.send_video_talker.repository.SendVideoTalkerAPI;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

/**
 * Created by User on 8/16/2017.
 */

public class SendVideoTalkerModel {

    private final AppCompatActivity activity;

    public SendVideoTalkerModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current talker
    public Observable<User> getTalker(String talkerId) {
        return SendVideoTalkerAPI.getTalkerUser(talkerId);
    }

    //Get the current user
    public Observable<User> getCurrentUser() {
        return SendVideoTalkerAPI.getCurrentUser();
    }

    //VIDEO MESSAGE
    //Method that call API to send image to Firebase
    public void uploadVideoToFirebase(User user, Uri fileName, String description, String duration, int videoSize) {
        SendVideoTalkerAPI.uploadVideoToFirebase(user, fileName, description, duration, activity, videoSize);
    }

    public void backToChatUserActivity(){
        activity.finish();
    }

    public void backToCamTalker(String mTalkerId){
        Intent intent = new Intent(activity, CamTalkerActivity.class);
        intent.putExtra(CONTACT_ID, mTalkerId);
        activity.startActivity(intent);
        activity.finish();
    }

    public void backToChatTalker(String mTalkerId){
        Intent intent = new Intent(activity, ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, mTalkerId);
        activity.startActivity(intent);
        activity.finish();
    }

}
