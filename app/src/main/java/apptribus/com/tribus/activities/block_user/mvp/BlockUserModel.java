package apptribus.com.tribus.activities.block_user.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.block_user.adapter.BlockUserAdapter;
import apptribus.com.tribus.activities.block_user.repository.BlockUserAPI;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;


public class BlockUserModel {

    public final AppCompatActivity activity;
    private FirestoreService mFirestoreService;
    private FirebaseAuth mAuth;


    public BlockUserModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<User> getUser() {
        return mFirestoreService.getCurrentUser(mAuth.getCurrentUser().getUid());
    }


    public Observable<List<Talk>> getAllContacts(List<Talk> contacts){
        return BlockUserAPI.getAllContacts(contacts);
    }

    public void loadMoreContacts(List<Talk> contacts, BlockUserAdapter blockUserAdapter,
                                 ProgressBar mProgressBarBottom){
        BlockUserAPI.loadMoreContacts(contacts, blockUserAdapter, mProgressBarBottom);
    }

    public void showDialogToRemoveContact(User contact){
        BlockUserAPI.showDialog(contact, activity);
    }

    public void openDetailContactActivity(String contactId, String tribuKey){

        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, contactId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }

    public void backToMainActivity(){
        activity.finish();
    }

}
