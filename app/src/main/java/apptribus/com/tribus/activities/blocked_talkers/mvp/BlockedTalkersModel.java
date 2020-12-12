package apptribus.com.tribus.activities.blocked_talkers.mvp;

import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.blocked_talkers.repository.BlockedTalkersAPI;
import apptribus.com.tribus.activities.blocked_talkers.view_holder.BlockedTalkersViewHolder;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

/**
 * Created by User on 7/11/2017.
 */

public class BlockedTalkersModel {

    private final AppCompatActivity activity;

    public BlockedTalkersModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //OBSERVABLES
    //Get the current talker
    public Observable<User> getUser() {
        return BlockedTalkersAPI.getUser();
    }

    //SET ADAPTER TO SHOW LIST OF BLOCKED TALKERS
    public FirebaseRecyclerAdapter<Talk, BlockedTalkersViewHolder> setRecyclerViewBlockedTalkers(){
        return BlockedTalkersAPI.getTalkers(activity);
    }

    public Observable<Boolean> hasChildren(){
        return BlockedTalkersAPI.hasChildren();
    }


    public void backToMainActivity(){
        activity.finish();
    }

}
