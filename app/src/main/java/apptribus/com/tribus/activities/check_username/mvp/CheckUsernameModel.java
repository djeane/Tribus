package apptribus.com.tribus.activities.check_username.mvp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.activities.check_username.repository.CheckUsernameAPI;
import apptribus.com.tribus.activities.new_register_user.RegisterUserActivity;
import apptribus.com.tribus.activities.privacy_policy.PrivacyPolicyCheckUsernameActivity;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

public class CheckUsernameModel {

    public final AppCompatActivity activity;
    private FirestoreService mFirestoreService;


    public CheckUsernameModel(AppCompatActivity activity) {
        this.activity = activity;
        mFirestoreService = new FirestoreService(activity);
    }

    public Observable<String> verifyUsername(String username) {
        return mFirestoreService.verifyUsername(username);
    }

    public void salveUsernameSharedPreference(String username){
        CheckUsernameAPI.salveUsernameSharedPreference(activity, username);
    }

    public void salveNomeSharedPreference(String name){
        CheckUsernameAPI.saveNameSharedPreference(activity, name);
    }

    public void startRegisterUserActivity(){
        Intent intent = new Intent(activity, RegisterUserActivity.class);
        activity.startActivity(intent);
    }

    public void openPrivacyPolicyActivity() {
        Intent intent = new Intent(activity, PrivacyPolicyCheckUsernameActivity.class);
        activity.startActivity(intent);
    }

}
