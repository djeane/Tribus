package apptribus.com.tribus.activities.phone_number_authentication.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.NAME_USER;
import static apptribus.com.tribus.util.Constantes.USERNAME_USER;

/**
 * Created by User on 9/10/2017.
 */

@SuppressLint("ViewConstructor")
public class PhoneNumberAuthenticationView extends RelativeLayout {

    @BindView(R.id.et_phone_number)
    EditText mEtPhoneNumber;

    @BindView(R.id.btn_resend)
    Button mBtnResend;

    @BindView(R.id.btn_send)
    Button mBtnSend;

    @BindView(R.id.btn_send_verification_code)
    Button mBtnSendVerificationCode;

    @BindView(R.id.tv_info_verification_code)
    TextView mTvInfoVerificationCode;

    @BindView(R.id.et_verification_code)
    EditText mEtVerificationCode;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.tv_info_phone)
    TextView mTvInfoPhoneNumber;

    public AppCompatActivity mContext;
    public String mNameUser, mUsernameUser;
    public Uri mImageUserUri;

    public PhoneNumberAuthenticationView(AppCompatActivity activity) {
        super(activity);
        mContext = activity;

        inflate(activity, R.layout.activity_phone_number_authentication, this);

        ButterKnife.bind(this);

        mNameUser = activity.getIntent().getExtras().getString(NAME_USER);
        mUsernameUser = activity.getIntent().getExtras().getString(USERNAME_USER);
        mImageUserUri = activity.getIntent().getData();


    }

    public Observable<Void> observableBtnSend(){
        return RxView.clicks(mBtnSend);
    }

    public Observable<Void> observableBtnResend(){
        return RxView.clicks(mBtnResend);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observableBtnSendVerificationCode(){
        return RxView.clicks(mBtnSendVerificationCode);
    }

    public Observable<Void> observableTvInfoNumber(){
        return RxView.clicks(mTvInfoPhoneNumber);
    }
}
