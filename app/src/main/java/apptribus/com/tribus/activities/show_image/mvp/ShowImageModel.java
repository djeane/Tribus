package apptribus.com.tribus.activities.show_image.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.camera.CamActivity;
import apptribus.com.tribus.activities.show_image.repository.ShowImageAPI;
import apptribus.com.tribus.activities.show_video.repository.ShowVideoAPI;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/20/2017.
 */

public class ShowImageModel {

    private final AppCompatActivity activity;

    public ShowImageModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return ShowImageAPI.getTribu(uniqueName);
    }

    //Get the current message
    public Observable<MessageUser> getMessage(String uniqueName, String messageReference){
        return ShowImageAPI.getMessage(uniqueName, messageReference);
    }

    public void backToChatTribuActivity(){
        activity.finish();
    }



}
