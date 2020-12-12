package apptribus.com.tribus.activities.detail_add_talker.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_add_talker.adapter.DetailAddTalkerAdapter;
import apptribus.com.tribus.activities.detail_add_talker.repository.DetailAddTalkerAPI;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class DetailAddTalkerModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;
    private FirestoreService mFirestoreService;


    public DetailAddTalkerModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<User> getUser(){
        return mFirestoreService.getCurrentUser(mAuth.getCurrentUser().getUid());
    }


    public Observable<List<Talk>> getAllContacts(List<Talk> contacts){
        return DetailAddTalkerAPI.getAllContacts(contacts);
    }

    public void loadMoreContacts(List<Talk> contacts, DetailAddTalkerAdapter detailAddTalkerAdapter,
                                  ProgressBar mProgressBarBottom){
        DetailAddTalkerAPI.loadMoreContacts(contacts, detailAddTalkerAdapter, mProgressBarBottom);
    }

    public void showDialogToAcceptContact(Talk contact, User userContact){
        DetailAddTalkerAPI.showDialogToAcceptContact(contact, userContact, activity);
    }

    public void showDialogToDenyInvitation(Talk contact, User userContact){
        DetailAddTalkerAPI.showDialogToDenyInvitation(contact, userContact, activity);
    }


    public void backToMainActivity(){
        activity.finish();
    }


    public void openDetailContactActivity(String userContactId, String tribuKey){
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, userContactId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }

}
