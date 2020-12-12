package apptribus.com.tribus.activities.profile_tribu_user.mvp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.profile_tribu_user.adapter.ProfileTribuUserAdapter;
import apptribus.com.tribus.activities.profile_tribu_user.repository.ProfileTribuUserAPI;
import apptribus.com.tribus.activities.show_profile_image.ShowProfileImageActivity;
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

public class ProfileTribuUserModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;


    private FirestoreService mFirestoreService;


    public ProfileTribuUserModel(AppCompatActivity activity) {
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
        return ProfileTribuUserAPI.getAllFollowers(followers, tribuKey);
    }

    public void loadMoreFollowers(List<Follower> followers, ProfileTribuUserAdapter profileTribuUserAdapter,
                                  ProgressBar mProgressBarBottom, String tribuKey) {
        ProfileTribuUserAPI.loadMoreFollowers(followers, profileTribuUserAdapter, mProgressBarBottom, tribuKey);
    }

    public void openShowProfileActivity(String image) {
        Intent intent = new Intent(activity, ShowProfileImageActivity.class);
        intent.setData(Uri.parse(image));
        activity.startActivity(intent);
    }


    public void backToMainActivity() {
        activity.finish();
    }


    //CREATE AND GET FOLLOWER
    public void creatFollower(Tribu tribu, AppCompatButton mBtnFollow) {
        ProfileTribuUserAPI.showDialog(activity, tribu, mBtnFollow);
    }

    public Observable<Boolean> getFollowerToSetButton(String tribuKey) {
        return ProfileTribuUserAPI.getFollowerToSetButton(tribuKey);
    }

    public Observable<Boolean> setButtonIfWaitingPermission(String mTribusKey) {
        return ProfileTribuUserAPI.setButtonIfWaitingPermission(mTribusKey);
    }

    public void openTribusImagesOrVideosFolderActivity(String mTribuKey, String intentExtra) {
        if (intentExtra.equals("image")) {
            Intent intent = new Intent(activity, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuUser");
            intent.putExtra(TRIBU_UNIQUE_NAME, mTribuKey);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(activity, TribusImagesFolderActivity.class);
            intent.putExtra("intentExtra", intentExtra);
            intent.putExtra("cameFrom", "fromProfileTribuUser");
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