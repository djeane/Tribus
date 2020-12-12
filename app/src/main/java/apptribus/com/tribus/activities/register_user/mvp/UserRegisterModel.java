package apptribus.com.tribus.activities.register_user.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import apptribus.com.tribus.activities.check_username.NewCheckUsernameActivity;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.activities.register_user.repository.UserAPI;
import rx.Observable;

/**
 * Created by User on 5/20/2017.
 */

public class UserRegisterModel {

    private final AppCompatActivity activity;
    public static int GALLERY_REQUEST = 1;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public UserRegisterModel(AppCompatActivity activity) {
        this.activity = activity;
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


    //OPEN GALLERY ACTIVITY AND CHOOSE AN IMAGE
    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.d("Valor: ", " intent no model: " + intent);
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }


    //CREATE USER IN FIREBASE DATABASE
    public boolean createUser(User user){
        Log.d("Valor: ", "createUser - model");
        return UserAPI.createUser(user);
    }


    //SAVE USERNAME IN FIREBASE DATABASE
    public boolean saveUsername(User user){
        return UserAPI.saveUsername(user.getName(), user.getUsername(), user.getImageUrl());
    }


    //GET A USER IN FIREBASE DATABASE(OBSERVABLE)
    public Observable<User> getUser(){
        Log.d("Valor: ", "getAdmin - model");
        return UserAPI.getUser();
    }


    //STORE IMAGE USER IN FIREBASE DATABASE
    public boolean storeImageUser(User user){
        return UserAPI.storeImage(user.getUsername(), Uri.parse(user.getImageUrl()));
    }


    //START MAIN ACTIVITY
    public void startMainActivity(){
        MainActivity.start(activity);
        activity.finish();
    }

    public void backToCheckUsernameActivity(){
        Intent intent = new Intent(activity, NewCheckUsernameActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
