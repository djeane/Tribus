package apptribus.com.tribus.activities.feature_choice_tribus.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.change_admin.ChangeAdminActivity;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.detail_tribu_add_followers.DetailTribuAddFollowersActivity;
import apptribus.com.tribus.activities.feature_choice_tribus.repository.FeatureChoiceTribusAPI;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_follower.ProfileTribuFollowerActivity;
import apptribus.com.tribus.activities.survey.SurveyActivity;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.SURVEY_QUESTION;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TOPIC_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

public class FeatureChoiceTribusModel {

    private final AppCompatActivity activity;
    private FirestoreService mFirestoreService;

    public FeatureChoiceTribusModel(AppCompatActivity activity) {
        this.activity = activity;
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }

    public void backToMainActivity(){
        activity.finish();
    }


    public void openDetailTribuAddFollowers(String mTribusKey) {
        Intent intent = new Intent(activity, DetailTribuAddFollowersActivity.class);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        activity.startActivity(intent);
    }

    public void openChangeAdminActivity(String tribuKey) {
        Intent intent = new Intent(activity, ChangeAdminActivity.class);
        intent.putExtra(TRIBU_KEY, tribuKey);
        activity.startActivity(intent);
    }

    public void openProfileTribuAdminActivity(String mTribusKey) {
        Intent intent = new Intent(activity, ProfileTribuAdminActivity.class);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        activity.startActivity(intent);
    }

    public void openProfileTribuFollowerActivity(String mTribusKey) {
        Intent intent = new Intent(activity, ProfileTribuFollowerActivity.class);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        activity.startActivity(intent);
    }


    public void leaveTribu(Tribu tribu) {
        FeatureChoiceTribusAPI.leaveTribu(activity, tribu);
    }


    public void openChatTribuActivity(String tribuUniqueName, String topicKey, String tribuKey, String tribuName, String topicName) {
        Intent intent = new Intent(activity, ChatTribuActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
        intent.putExtra(TOPIC_KEY, topicKey);
        intent.putExtra(TOPIC_NAME, topicName);
        intent.putExtra(TRIBU_NAME, tribuName);
        intent.putExtra(TRIBU_KEY, tribuKey);
        activity.startActivity(intent);
    }
    public void openSurveyActivity(Tribu mTribu, String question) {

        Intent intent = new Intent(activity, SurveyActivity.class);
        intent.putExtra(TRIBU_KEY, mTribu.getKey());
        intent.putExtra(SURVEY_QUESTION, question);
        activity.startActivity(intent);
    }

    public void sendTopicToFirebase(Tribu tribu, ConversationTopic topic){
        FeatureChoiceTribusAPI.sendTopicToFirebase(tribu, topic, activity);
    }


}
