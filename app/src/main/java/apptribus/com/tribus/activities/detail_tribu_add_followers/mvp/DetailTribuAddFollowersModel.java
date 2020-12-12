package apptribus.com.tribus.activities.detail_tribu_add_followers.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.detail_tribu_add_followers.adapter.DetailTribuAddFollowersAdapter;
import apptribus.com.tribus.activities.detail_tribu_add_followers.repository.DetailTribuAddFollowersAPI;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;


public class DetailTribuAddFollowersModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;


    public DetailTribuAddFollowersModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
    }


    public Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {
        return DetailTribuAddFollowersAPI.getAllFollowers(followers, tribuKey);
    }

    public void loadMoreFollowers(List<Follower> followers, DetailTribuAddFollowersAdapter detailTribuAddFollowersAdapter,
                                  ProgressBar mProgressBarBottom, String tribuKey) {
        DetailTribuAddFollowersAPI.loadMoreFollowers(followers, detailTribuAddFollowersAdapter, mProgressBarBottom, tribuKey);
    }

    public void showDialogToDeny(Follower follower, User userFollower, String tribuKey) {
        DetailTribuAddFollowersAPI.showDialogToDeny(follower, userFollower, tribuKey, activity);
    }

    public void showDialogToAccept(Follower follower, User userFollower, String tribuKey) {
        DetailTribuAddFollowersAPI.showDialogToAccept(follower, userFollower, tribuKey, activity);
    }

    public void backToMainActivity() {
        activity.finish();
    }

    public void openDetailFollowerActivity(String userFollowerId, String tribuKey) {
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, userFollowerId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }


}
