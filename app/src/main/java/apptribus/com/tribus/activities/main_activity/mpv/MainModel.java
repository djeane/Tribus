package apptribus.com.tribus.activities.main_activity.mpv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import apptribus.com.tribus.activities.block_user.BlockUserActivity;
import apptribus.com.tribus.activities.blocked_talkers.BlockedTalkersActivity;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.create_tribu.CreateTribuActivity;
import apptribus.com.tribus.activities.detail_add_talker.DetailAddTalkerActivity;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.feature_choice_tribus.FeatureChoiceTribusActivity;
import apptribus.com.tribus.activities.invitation_request_tribu.InvitationRequestTribuActivity;
import apptribus.com.tribus.activities.invitations_request_user.InvitationRequestUserActivity;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder.ThematicsViewHolder;
import apptribus.com.tribus.activities.main_activity.repository.MainAPI;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder.FoldersTribusViewHolder;
import apptribus.com.tribus.activities.privacy_policy.PrivacyPolicyCheckUsernameActivity;
import apptribus.com.tribus.activities.profile_tribu_user.ProfileTribuUserActivity;
import apptribus.com.tribus.activities.user_profile.UserProfileActivity;
import apptribus.com.tribus.pojo.Admin;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.USER_ID;

/**
 * Created by User on 5/25/2017.
 */

public class MainModel {

    public final AppCompatActivity activity;

    //FIREBASE AUTH
    private FirebaseAuth mAuth;

    //FIRESTORE SERVICE
    private FirestoreService mFirestoreService;


    public MainModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    public void getAllContactsMessages(){
        mFirestoreService.getAllContactsMessages();
    }
    public void getAllParticipanting(){
        mFirestoreService.getAllParticipanting();
    }


    //GET CURRENT USER FROM FIRESTORE USING FIRESTORE SERVICE
    public Observable<User> getUser(){
        return mFirestoreService.getCurrentUser(mAuth.getCurrentUser().getUid());
    }

    /*public void addTribuUniqueNameIntoFirestore(){
        mFirestoreService.addTribuUniqueNameIntoFirestore();
    }*/

    public void addUsersNameIntoFirestore(){
        mFirestoreService.addUsernameIntoFirestore();
    }

    public void addTribuIntoFirestore(){
        //mFirestoreService.addTribuIntoFirestore();
    }

    public void getAllContacts(){
        //mFirestoreService.getAllContacts();
    }

    public void openShareFragmentToCard(Tribu tribu){
        MainAPI.openShareFragmentToCard(tribu, activity);
    }

    public void openUserProfile() {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);

    }

    public void openPrivacyPolicyActivity() {
        Intent intent = new Intent(activity, PrivacyPolicyCheckUsernameActivity.class);
        activity.startActivity(intent);

    }

    public void openShareFragmetToApp(){
        MainAPI.openShareFragmentToApp(activity);
    }



    public void openCreateTribuActivity(){
        CreateTribuActivity.start(activity);
    }

    public void updateLastMessage(String mTalkerId){
        MainAPI.updateLastMessage(mTalkerId);
    }

    public void createThumbImage(String imageUrl, User currentUser){
        MainAPI.createThumbImage(activity, imageUrl, currentUser);
    }

    public void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName){
        Intent intent = new Intent(activity, FeatureChoiceTribusActivity.class);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribuUniqueName);
        activity.startActivity(intent);
    }

    public void openChatUserActivity(String contactId){
        Intent intent = new Intent(activity, ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, contactId);
        activity.startActivity(intent);
    }

    public void openDetailContactActivity(String contactId, String tribuKey){

        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, contactId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }

    public void openProfileTribuUserActivity(Tribu tribu) {
        Intent intent = new Intent(activity, ProfileTribuUserActivity.class);
        intent.putExtra(TRIBU_KEY, tribu.getKey());
        activity.startActivity(intent);
    }
}
