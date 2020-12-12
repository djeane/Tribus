package apptribus.com.tribus.activities.user_profile.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.main_activity.fragment_timeline.adapter.TribusLineAdapter;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.repository.TimeLineAPI;
import apptribus.com.tribus.activities.show_profile_image.ShowProfileImageActivity;
import apptribus.com.tribus.activities.user_profile.adapter.UserProfileUpdatesAdapter;
import apptribus.com.tribus.activities.user_profile.repository.UserProfileAPI;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.UserUpdate;
import apptribus.com.tribus.util.OnClickListenerAdapterTribusThematics;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

public class UserProfileModel {

    public final AppCompatActivity activity;

    //TO REQUEST PERMISSIONS
    public static int GALLERY_REQUEST = 1;
    public static int CAMERA_REQUEST = 2;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FirestoreService mFirestoreService;
    private FirebaseAuth mAuth;


    public UserProfileModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<List<UserUpdate>> getAllUpdates(List<UserUpdate> updates){
        return UserProfileAPI.getAllUpdates(updates);
    }

    public void loadMoreUpdates(List<UserUpdate> updates, UserProfileUpdatesAdapter mUserProfileUpdatesAdapter,
                                ProgressBar mProgressBarBottom){
        UserProfileAPI.loadMoreUpdates(updates, mUserProfileUpdatesAdapter, mProgressBarBottom);
    }


    //GET USER
    public Observable<User> getUser(){
        return UserProfileAPI.getUser(activity);

    }

    public Observable<List<Tribu>> getAdminsNumber(List<Tribu> tribus){
        return mFirestoreService.getAdminsNumber(tribus, mAuth.getCurrentUser().getUid());
    }

    public void backToMainActivity(){
        activity.finish();
    }

    public void openShowProfileImageActivity(String image){
        Intent intent = new Intent(activity, ShowProfileImageActivity.class);
        intent.setData(Uri.parse(image));
        activity.startActivity(intent);
    }


    //Get the num tribus
    public Observable<Integer> getNumTribus() {
        return mFirestoreService.getNumTribus(mAuth.getCurrentUser().getUid());
        //return UserProfileAPI.getNumTribus();
    }

    //Get the num contacts
    public Observable<Integer> getNumContacts() {
        return mFirestoreService.getNumContacts(mAuth.getCurrentUser().getUid());
        //return UserProfileAPI.getNumContacts();
    }


    //UPDATE NAME
    public void updateName(String name, AlertDialog dialog){
        UserProfileAPI.updateName(name, dialog, activity);
    }

    //UPDATE ABOUT ME
    public void updateAboutMe(String aboutMe){
        UserProfileAPI.updateAboutMe(aboutMe);
    }

    //UPDATE AGE
    public void updateAge(int year, int month, int day){
        UserProfileAPI.updateAge(year, month, day);
    }

    //UPDATE AGE
    public void updateGender(String gender){
        UserProfileAPI.updateGender(gender);
    }


    //UPDATE PERMISSIONS
    public void updatePermissions(boolean isAccepted, CompoundButton toggleButton){
        UserProfileAPI.showDialogForIsAccepted(activity, isAccepted, toggleButton);
    }

    //VERIFY USERNAME
    public Observable<String> verifyUsername(String username){
        return mFirestoreService.verifyUsername(username);
    }

    //UPDATE USERNAME
    public void updateUsername(String username, AlertDialog dialog){
        UserProfileAPI.updateUsername(activity, username, dialog);
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

    public void uploadImageToFirebase(Uri image){
        UserProfileAPI.uploadImageToFirebase(activity, image);
    }

}
