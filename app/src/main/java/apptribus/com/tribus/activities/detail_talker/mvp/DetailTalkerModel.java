package apptribus.com.tribus.activities.detail_talker.mvp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.repository.DetailTalkerAPI;
import apptribus.com.tribus.activities.show_profile_image.ShowProfileImageActivity;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;


public class DetailTalkerModel {

    public final AppCompatActivity activity;
    private FirestoreService mFirestoreService;

    public DetailTalkerModel(AppCompatActivity activity) {
        this.activity = activity;
        mFirestoreService = new FirestoreService(activity);
    }


    //OBSERVABLES
    //Get the user no metter is contanct
    public Observable<User> getUserNoMetterIsContact(String contactId) {
        return mFirestoreService.getCurrentUser(contactId);
    }

    //if user is contact
    public Observable<User> getContact(String contactId, String currentUserId) {
        return mFirestoreService.getContactUser(contactId, currentUserId);
    }

    public Observable<List<Tribu>> getAdminsNumber(List<Tribu> tribus, String contactId){
        return mFirestoreService.getAdminsNumber(tribus, contactId);
    }


    public Observable<User> getUserWaitingPermission(String contactId, String currentUserId) {
        return mFirestoreService.getUserWaitingPermission(contactId, currentUserId);
    }

    public Observable<User> getUserInvited(String contactId, String currentUserId) {
        return mFirestoreService.getUserInvited(contactId, currentUserId);
    }


    public Observable<User> getUserThatIsNotAContact(String currentUserId, String userNotAContactId) {
        return mFirestoreService.getUserThatIsNotAContact(currentUserId, userNotAContactId);
    }

    //Get number of tribus
    public Observable<Integer> getNumTribus(String contactId) {
        return mFirestoreService.getNumTribus(contactId);
    }

    //Get number of contacts
    public Observable<Integer> getNumContacts(String contactId) {
        return mFirestoreService.getNumContacts(contactId);
    }

    public void removeContact(String talkerId, String mFromChatTribus) {
        DetailTalkerAPI.showDialogToRemoveContact(activity, talkerId, mFromChatTribus);
    }


    public void addContact(String tribusKey, String mContactId, DetailTalkerView view){
        DetailTalkerAPI.showDialogAddContact(tribusKey, activity, mContactId, view);
    }

    public void addContactIfInvitedAndProfilePrivate(String tribusKey, String mContactId, DetailTalkerView view){
        DetailTalkerAPI.addContactIfInvitedAndProfilePrivate(tribusKey, activity, mContactId, view);
    }

    public void excludeInvitationIfProfilePrivateAndPublic(String mTalkerId, DetailTalkerView view){
        DetailTalkerAPI.excludeInvitationIfProfilePrivateAndPublic(activity, mTalkerId, view);
    }

    public void showDialogAddTalkerIfPrivate(String tribusKey, String mTalkerId, DetailTalkerView view){
        DetailTalkerAPI.showDialogAddTalkerIfPrivate(tribusKey, activity, mTalkerId, view);
    }

    public void cancelInvitationTalkerPrivate(String mTalkerId, DetailTalkerView view){
        DetailTalkerAPI.cancelInvitationTalkerPrivate(activity, mTalkerId, view);
    }

    public void backToMainActivity(){
        activity.finish();
    }


    public void openShowProfileImageActivity(String imageUrl) {
        Intent intent = new Intent(activity, ShowProfileImageActivity.class);
        intent.setData(Uri.parse(imageUrl));
        activity.startActivity(intent);
    }
}
