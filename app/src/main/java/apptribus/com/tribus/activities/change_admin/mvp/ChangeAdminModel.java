package apptribus.com.tribus.activities.change_admin.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.change_admin.adapter.ChangeAdminAdapter;
import apptribus.com.tribus.activities.change_admin.repository.ChangeAdminAPI;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class ChangeAdminModel {

    private final AppCompatActivity activity;
    private FirestoreService mFirestoreService;
    private FirebaseAuth mAuth;


    public ChangeAdminModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }


    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }

    public Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey){
        return ChangeAdminAPI.getAllFollowers(followers, tribuKey);
    }

    public void loadMoreFollowers(List<Follower> followers, ChangeAdminAdapter changeAdminAdapter,
                                  ProgressBar mProgressBarBottom, String tribuKey){
        ChangeAdminAPI.loadMoreFollowers(followers, changeAdminAdapter, mProgressBarBottom, tribuKey);
    }

    public void openDialogToChangeAdmin(User follower, String tribuKey){
        ChangeAdminAPI.openDialogChangeAdmin(follower, tribuKey, activity);
    }

    public void backToChatTribuActivity() {
        activity.finish();
    }


    public void openDetailActivity(String userFollowerId, String tribuKey){
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, userFollowerId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }


}
