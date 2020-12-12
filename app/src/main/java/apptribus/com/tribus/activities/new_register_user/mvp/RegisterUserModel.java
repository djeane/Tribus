package apptribus.com.tribus.activities.new_register_user.mvp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import apptribus.com.tribus.activities.phone_number_authentication.PhoneNumberAuthenticationActivity;
import apptribus.com.tribus.activities.privacy_policy.PrivacyPolicyActivity;

import static apptribus.com.tribus.util.Constantes.NAME_USER;
import static apptribus.com.tribus.util.Constantes.USERNAME_USER;

/**
 * Created by User on 11/19/2017.
 */

public class RegisterUserModel {

    private final AppCompatActivity activity;
    public static int GALLERY_REQUEST = 1;
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public RegisterUserModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    public boolean requirePermission() {
        return verifyStoragePermissions(activity);

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

    //OPEN GALLERY ACTIVITY AND CHOOSE AN IMAGE
    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void openPhoneNumberAuthentication(Uri imageUser, String name, String userName){
        Intent intent = new Intent(activity, PhoneNumberAuthenticationActivity.class);
        intent.putExtra(NAME_USER, name);
        intent.putExtra(USERNAME_USER, userName);
        intent.setData(imageUser);
        activity.startActivity(intent);
        activity.finish();
    }

    public void openPrivacyPolicyActivity() {
        Intent intent = new Intent(activity, PrivacyPolicyActivity.class);
        activity.startActivity(intent);
    }


}
