package apptribus.com.tribus.activities.conversation_topics.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import apptribus.com.tribus.activities.change_admin.ChangeAdminActivity;
import apptribus.com.tribus.activities.conversation_topics.repository.ConversationTopicAPI;
import apptribus.com.tribus.activities.conversation_topics.view_holder.ConversationTopicVH;
import apptribus.com.tribus.activities.detail_tribu_add_followers.DetailTribuAddFollowersActivity;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_follower.ProfileTribuFollowerActivity;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/25/2017.
 */

public class ConversationTopicsModel {

    private final AppCompatActivity activity;

    public ConversationTopicsModel(AppCompatActivity activity) {
        this.activity = activity;
    }


    public Observable<Tribu> getTribu(String uniqueName) {
        return ConversationTopicAPI.getTribu(uniqueName);
    }

    public void openDetailTribu(Tribu tribu) {
        Intent intent = new Intent(activity, ProfileTribuFollowerActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void openDetailTribuAddFollowers(Tribu tribu) {
        Intent intent = new Intent(activity, DetailTribuAddFollowersActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void openChangeAdminActivity(Tribu tribu) {
        Intent intent = new Intent(activity, ChangeAdminActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void openProfileTribuAdminActivity(Tribu tribu) {
        Intent intent = new Intent(activity, ProfileTribuAdminActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void leaveTribu() {
        ConversationTopicAPI.leaveTribu(activity);
    }


    public void sendTopicToFirebase(Tribu tribu, ConversationTopic topic){
        ConversationTopicAPI.sendTopicToFirebase(tribu, topic, activity);
    }

    //SET ADAPTER TO SHOW LIST OF TALKERS
    public FirebaseRecyclerAdapter<ConversationTopic, ConversationTopicVH> setRecyclerView(Tribu tribu, ConversationTopicsView view){
        return ConversationTopicAPI.getConversationTopics(tribu, view);
    }

    //Get the current talker
    public Observable<User> getUser() {
        return ConversationTopicAPI.getUser();
    }

    public Observable<Boolean> hasChildren(String mTribusKey){
        return ConversationTopicAPI.hasChildren(mTribusKey);
    }


    public void createConversationTopic(String mTribuKey, String mTopicKey) {
        //ConversationTopicAPI.createConversationTopic(mTribuKey, mTopicKey);
    }

    public void removeListeners(){
        if (ConversationTopicAPI.mValueListenerRefUser != null){
            ConversationTopicAPI.mReferenceUser.removeEventListener(ConversationTopicAPI.mValueListenerRefUser);
        }
        if (ConversationTopicAPI.mValueListenerRefTribusMessages != null){
            ConversationTopicAPI.mRefTribusMessages.removeEventListener(ConversationTopicAPI.mValueListenerRefTribusMessages);
        }
        if (ConversationTopicAPI.mValueListenerRefTribusTopic != null){
            ConversationTopicAPI.mRefTribusTopics.removeEventListener(ConversationTopicAPI.mValueListenerRefTribusTopic);
        }
    }
}
