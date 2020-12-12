package apptribus.com.tribus.activities.show_image_talker.mvp;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_image.repository.ShowImageAPI;
import apptribus.com.tribus.activities.show_image_talker.repository.ShowImageTalkerAPI;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

/**
 * Created by User on 8/16/2017.
 */

public class ShowImageTalkerModel {

    private final AppCompatActivity activity;

    public ShowImageTalkerModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current talker
    public Observable<User> getTalkerUser(String talkerKey) {
        return ShowImageTalkerAPI.getTalkerUser(talkerKey);
    }

    //Get the current message
    public Observable<MessageUser> getMessage(String uniqueName, String messageReference){
        return ShowImageTalkerAPI.getMessage(uniqueName, messageReference);
    }

    public void backToChatTalkerActivity(){
        activity.finish();
    }


}
