package apptribus.com.tribus.activities.phone_number_authentication.mvp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.PhoneAuthProvider;

import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.phone_number_authentication.repository.PhoneNumberAuthenticationAPI;
import apptribus.com.tribus.pojo.User;

/**
 * Created by User on 9/10/2017.
 */

public class PhoneNumberAuthenticationModel {

    private final AppCompatActivity activity;

    public PhoneNumberAuthenticationModel(AppCompatActivity activity) {
        this.activity = activity;
    }


    public void setUpVerificationCallbacksSendCode(Button mBtnResend, Button mBtnSend, Button mBtnSendVerificationCode,
                                                   TextView mTvInfoVerificationCode, EditText mEtVerificationCode,
                                                   String mPhoneNumber,
                                                   PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks,
                                                   ProgressDialog progress){
        PhoneNumberAuthenticationAPI.setUpVerificationCallbacksSendCode(mBtnResend, mBtnSend, mBtnSendVerificationCode,
                mTvInfoVerificationCode, mEtVerificationCode, mPhoneNumber, activity, mVerificationCallbacks, progress);
    }

    public void setUpVerificationCallbacksResendCode(Button mBtnResend, Button mBtnSend, Button mBtnSendVerificationCode,
                                                     TextView mTvInfoVerificationCode, EditText mEtVerificationCode,
                                                     String mPhoneNumber,
                                                     PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks,
                                                     ProgressDialog progress){
        PhoneNumberAuthenticationAPI.setUpVerificationCallbacksResendCode(mBtnResend, mBtnSend, mBtnSendVerificationCode,
                mTvInfoVerificationCode, mEtVerificationCode, mPhoneNumber, activity, mVerificationCallbacks, progress);
    }


    //CREATE USER IN FIREBASE DATABASE
    public boolean createUser(User user, String code, ProgressDialog progress, Button mBtnSendVerificationCode){
        return PhoneNumberAuthenticationAPI.createUser(user, code, activity, progress, mBtnSendVerificationCode);
    }

    //START MAIN ACTIVITY
    public void startMainActivity(){
        MainActivity.start(activity);
        activity.finish();
    }

}
