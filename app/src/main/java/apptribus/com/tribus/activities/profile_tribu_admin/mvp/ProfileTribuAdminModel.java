package apptribus.com.tribus.activities.profile_tribu_admin.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.profile_tribu_admin.adapter.ProfileTribuAdminAdapter;
import apptribus.com.tribus.activities.profile_tribu_admin.repository.ProfileTribuAdminAPI;
import apptribus.com.tribus.activities.show_profile_image.ShowProfileImageActivity;
import apptribus.com.tribus.activities.user_profile.UserProfileActivity;
import apptribus.com.tribus.pojo.Follower;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class ProfileTribuAdminModel {

    private final AppCompatActivity activity;
    private FirebaseAuth mAuth;


    private FirestoreService mFirestoreService;

    public ProfileTribuAdminModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    //TO REQUEST PERMISSIONS
    public static int GALLERY_REQUEST = 1;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }

    public Observable<User> getAdmin(Tribu tribu) {
        return mFirestoreService.getAdmin(tribu.getAdmin().getUidAdmin());
    }

    public Observable<List<Follower>> getAllFollowers(List<Follower> followers, String tribuKey) {
        return ProfileTribuAdminAPI.getAllFollowers(followers, tribuKey);
    }

    public void loadMoreFollowers(List<Follower> followers, ProfileTribuAdminAdapter profileTribuAdminAdapter,
                                  ProgressBar mProgressBarBottom, String tribuKey) {
        ProfileTribuAdminAPI.loadMoreFollowers(followers, profileTribuAdminAdapter, mProgressBarBottom, tribuKey);
    }


    //GET TRIBU INSIDE FOLLOWER
    public Observable<Tribu> getTribuFollower(Tribu tribu) {
        return mFirestoreService.getTribuFollower(mAuth.getCurrentUser().getUid(), tribu.getKey());
    }


    public void updateTribusDescription(String tribuKey, String description) {
        ProfileTribuAdminAPI.updateTribusDescription(activity, tribuKey, description);
    }

    //ASK FOR PERMISSION IF DON'T GIVE YET
    private static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean yes = false;

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
            yes = true;
        }
        return yes;
    }

    public boolean requirePermission() {
        return verifyStoragePermissions(activity);

    }

    public void showDialogToChangeIsPublic(String tribuKey, String isPublic, boolean option) {
        ProfileTribuAdminAPI.showDialogToChangeIsPublic(activity, tribuKey, isPublic, option);
    }

    //OPEN GALLERY ACTIVITY AND CHOOSE AN IMAGE
    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void uploadImageToFirebase(Uri image, String tribuKey) {
        ProfileTribuAdminAPI.uploadImageToFirebase(activity, image, tribuKey);
    }


    public void backToMainActivity() {
        activity.finish();
    }


    public void openShowProfileImageActivity(String imageUrl) {
        Intent intent = new Intent(activity, ShowProfileImageActivity.class);
        intent.setData(Uri.parse(imageUrl));
        activity.startActivity(intent);
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
