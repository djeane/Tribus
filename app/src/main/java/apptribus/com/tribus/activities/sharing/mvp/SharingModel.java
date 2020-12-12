package apptribus.com.tribus.activities.sharing.mvp;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by User on 12/12/2017.
 */

public class SharingModel {

    private final AppCompatActivity activity;

    public SharingModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void backToMainActivity(){
        activity.finish();
    }

}
