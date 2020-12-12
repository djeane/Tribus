package apptribus.com.tribus.activities.check_username.repository;

import android.support.v7.app.AppCompatActivity;

import apptribus.com.tribus.util.SharedPreferenceManager;

public class CheckUsernameAPI {


    //SAVE USERNAME TO SHARED PREFERENCES
    public static void salveUsernameSharedPreference(AppCompatActivity activity, String username){
        SharedPreferenceManager.getInstance(activity.getApplicationContext()).salvarUsernameUsuario(username);
    }

    //SAVE NAME TO SHARED PREFERENCES
    public static void saveNameSharedPreference(AppCompatActivity activity, String name){
        SharedPreferenceManager.getInstance(activity.getApplicationContext()).salvarNomeUsuario(name);
    }

}
