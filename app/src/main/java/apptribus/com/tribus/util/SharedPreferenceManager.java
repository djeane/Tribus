package apptribus.com.tribus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by User on 5/20/2017.
 */

public class SharedPreferenceManager {

    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String SHARED_PREF_DADOS_USUARIO = "preferencias_usuario";
    private static final String SHARED_PREF_DADOS_TRIBU = "preferencias_tribu";
    private static final String TAG_NAO_HOUVE_REGISTRO = "Não houve registro";

    //USUÁRIO
    private static final String TAG_USERNAME = "userName";
    private static final String TAG_NOME = "nome";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_TOKEN = "token";

    //TRIBU
    private static final String TAG_TRIBU_NAME = "tribuName";
    private static final String TAG_TRIBU_DESCRICAO = "tribuDescricao";
    private static final String TAG_TRIBU_TEMATICA = "tribuTematica";
    private static final String TAG_TRIBU_URI_IMAGE = "tribuImage";


    private static SharedPreferenceManager mInstance;
    private static Context mCtx;
    private SharedPreferences sharedPreferences;

    private SharedPreferenceManager(Context context) {
        mCtx = context;
        sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_DADOS_USUARIO, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferenceManager(context);
        }
        return mInstance;
    }


    public boolean saveDeviceToken(String token){
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getDeviceToken(){
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(TAG_TOKEN, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN, TAG_NAO_HOUVE_REGISTRO);
    }

    public boolean salvarNomeUsuario(String nome){
        //SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_DADOS_USUARIO, Context.MODE_PRIVATE)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_NOME, nome);
        editor.apply();
        Log.d("Valor: ", "SharedPref.: nome: " + nome);
        return true;
    }

    public boolean salvarUsernameUsuario(String username){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_USERNAME, username);
        editor.apply();
        Log.d("Valor: ", "SharedPref.: username: " + username);
        return true;
    }

    public String getUsername(){
        return sharedPreferences.getString(TAG_USERNAME, TAG_NAO_HOUVE_REGISTRO);
    }

    public String getNome(){
        return sharedPreferences.getString(TAG_NOME, TAG_NAO_HOUVE_REGISTRO);
    }

}
