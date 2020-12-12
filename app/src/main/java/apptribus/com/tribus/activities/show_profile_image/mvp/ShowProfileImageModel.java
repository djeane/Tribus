package apptribus.com.tribus.activities.show_profile_image.mvp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.show_profile_image.repository.ShowProfileImageAPI;

/**
 * Created by User on 12/25/2017.
 */

public class ShowProfileImageModel {

    private final AppCompatActivity activity;

    public ShowProfileImageModel(AppCompatActivity activity) {
        this.activity = activity;
    }


    public void downloadImage(Uri image){
        ShowProfileImageAPI.downloadImage(activity, image);
    }

    public void openShareImage(Uri image){
        ShowProfileImageAPI.openShareImage(activity, image);
    }

}
