package apptribus.com.tribus.activities.profile_tribu_follower.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.profile_tribu_follower.adapter.ProfileTribuFollowerAdapter;
import apptribus.com.tribus.activities.profile_tribu_follower.repository.ProfileTribuFollowerAPI;
import apptribus.com.tribus.activities.tribus_images_folder.TribusImagesFolderActivity;
import apptribus.com.tribus.activities.user_profile.UserProfileActivity;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class ProfileTribuFollowerModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;


    private FirestoreService mFirestoreService;

    public ProfileTribuFollowerModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }

    public Observable<User> getAdmin(Tribu tribu) {
        return mFirestoreService.getAdmin(tribu.getAdmin().getUidAdmin());
    }

    public Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {
        return ProfileTribuFollowerAPI.getAllFollowers(followers, tribuKey);
    }

    public void loadMoreFollowers(List<Follower> followers, ProfileTribuFollowerAdapter profileTribuFollowerAdapter,
                                  ProgressBar mProgressBarBottom, String tribuKey) {
        ProfileTribuFollowerAPI.loadMoreFollowers(followers, profileTribuFollowerAdapter, mProgressBarBottom, tribuKey);
    }


    public Observable<Tribu> getTribuFollower(Tribu tribu) {
        return mFirestoreService.getTribuFollower(mAuth.getCurrentUser().getUid(), tribu.getKey());
    }


    public void backToMainActivity() {
        activity.finish();
    }

    public void openTribusImagesOrVideosFolderActivity(String mTribuKey, String intentExtra) {
        if (intentExtra.equals("image")) {
            Intent intent = new Intent(activity, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuFollower");
            intent.putExtra(TRIBU_UNIQUE_NAME, mTribuKey);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuFollower");
            intent.putExtra(TRIBU_UNIQUE_NAME, mTribuKey);
            activity.startActivity(intent);
        }

    }


    public void openCurrentUserProfile() {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        activity.startActivity(intent);
    }

    public void openFollowerProfile(String userContactId, String tribuKey) {
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, userContactId);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, mAuth.getCurrentUser().getUid());
        activity.startActivity(intent);
    }

}
