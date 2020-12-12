package apptribus.com.tribus.activities.show_video.mvp;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.send_video.repository.SendVideoAPI;
import apptribus.com.tribus.activities.show_video.repository.ShowVideoAPI;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import rx.Observable;

/**
 * Created by User on 7/20/2017.
 */

public class ShowVideoModel {

    public final AppCompatActivity activity;

    public ShowVideoModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String uniqueName) {
        return ShowVideoAPI.getTribu(uniqueName);
    }

    //Get the current message
    public Observable<MessageUser> getMessage(String uniqueName, String messageReference){
        return ShowVideoAPI.getMessage(uniqueName, messageReference);
    }

    public void backToChatTribuActivity(){
        activity.finish();
    }

}
