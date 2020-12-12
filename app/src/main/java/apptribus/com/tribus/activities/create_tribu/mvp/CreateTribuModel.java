package apptribus.com.tribus.activities.create_tribu.mvp;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.twistedequations.rxstate.RxSaveState;

import apptribus.com.tribus.activities.create_tribu.repository.CreateTribuAPI;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.STATE_KEY;

public class CreateTribuModel{

    private final AppCompatActivity activity;
    public static int GALLERY_REQUEST = 1;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FirestoreService mFirestoreService;
    private FirebaseAuth mAuth;



    public CreateTribuModel(AppCompatActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mFirestoreService = new FirestoreService(activity);

    }

    public Observable<String> verifyTribuName(String uniqueName){
        return mFirestoreService.verifyTribuUniqueName(uniqueName);
    }

    //ASK FOR PERMISSION
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

    public Observable<User> getCurrentUser(){
        return mFirestoreService.getCurrentUser(mAuth.getCurrentUser().getUid());
    }


    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }


    public void createTribu(Tribu tribu){
        CreateTribuAPI.createTribu(tribu, activity);
    }


    public void saveUniqueName(Tribu tribu){
        CreateTribuAPI.saveUniqueName(tribu, activity);
    }

    public void saveAdmin(Tribu tribu){
        CreateTribuAPI.saveAdmin(tribu, activity);
    }

    public void saveParticipantAdmin(Tribu tribu){
        CreateTribuAPI.saveParticipantAdmin(tribu, activity);
    }

    public void updateThematics(Tribu tribu){
        CreateTribuAPI.updateThematics(tribu, activity);
    }

    public void storageImageTribu(Tribu tribu){
        CreateTribuAPI.storageImageTribu(tribu, activity);
    }


    //SAVING STATE
    public void saveState(String tribuName){
        RxSaveState.updateSaveState(activity, bundle -> {
            bundle.putString(STATE_KEY, tribuName);
        });
    }

    public Observable<String> getUserFromSaveState(){
        return RxSaveState.getSavedState(activity)
                .map(bundle -> bundle.getString(STATE_KEY));
    }


    //START MAIN ACTIVITY
    public void startMainActivity(){
        MainActivity.start(activity);
        activity.finish();
    }

    public void backToMainActivity(){
        activity.finish();
    }


}
