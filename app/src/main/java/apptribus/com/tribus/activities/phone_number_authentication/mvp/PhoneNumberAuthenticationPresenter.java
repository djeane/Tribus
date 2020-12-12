package apptribus.com.tribus.activities.phone_number_authentication.mvp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.INDIVIDUAL_USERS;

/**
 * Created by User on 9/10/2017.
 */

public class PhoneNumberAuthenticationPresenter {

    private final PhoneNumberAuthenticationView view;
    private final PhoneNumberAuthenticationModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private ProgressDialog progress;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private boolean response;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mVerificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mPhoneNumber, mCode;
    private String mMessageSendPhoneNumber, mMessageSendCode;
;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // [END declare_auth]

    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceIndvidualUser = mDatabase.getReference().child(INDIVIDUAL_USERS);



    public PhoneNumberAuthenticationPresenter(PhoneNumberAuthenticationView view, PhoneNumberAuthenticationModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openMainActivity();
        }
        else {
            progress = new ProgressDialog(view.mContext);
            progress.setCancelable(false);
            mMessageSendPhoneNumber = "Aguarde enquanto enviamos o seu código...";
            mMessageSendCode = "Aguarde enquanto seu cadastro é realizado...";

            setTvInfoPhoneNumber();

            //KEEP SYNCED
            mAuth = FirebaseAuth.getInstance();
            mReferenceUser.keepSynced(true);
            mReferenceIndvidualUser.keepSynced(true);

            subscription.add(observeBtnSend());
            subscription.add(observeBtnResend());
            subscription.add(observeBtnSendVerificationCode());
            subscription.add(observableTvInfoNumber());
            subscription.add(observeBtnArrowBack());

        }

        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();

    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
    }

    private void openMainActivity(){
        Intent intent = new Intent(view.mContext, MainActivity.class);
        view.mContext.startActivity(intent);
        view.mContext.finish();
    }

    private void setTvInfoPhoneNumber(){
        //index = 83 - 115
        String textInfo = "Informe o código de área seguido do número de telefone SEM o número '9' na frente. Por que precisamos do seu número?";

        SpannableString styledString = new SpannableString(textInfo);
        styledString.setSpan(
                new ForegroundColorSpan(view.mContext.getResources().getColor(R.color.colorAccent)),
                83,
                115,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        view.mTvInfoPhoneNumber.setText(styledString);

    }

    private Subscription observableTvInfoNumber(){
        return view.observableTvInfoNumber()
                .subscribe(__ -> openDialogInfo());
    }

    private void openDialogInfo() {

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        builder.setTitle("Sobre seu número de telefone...");
        builder.setMessage(view.getContext().getString(R.string.tv_info_phone_number));


        String positiveText = "OK, ENTENDI!";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private Subscription observeBtnSend(){
        return view.observableBtnSend()
                .doOnNext(__ -> view.mBtnSend.setEnabled(false))
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        view.mBtnSend.setEnabled(true);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(__ -> getPhoneNumber())
                .filter(phoneNumber -> {
                    if (TextUtils.isEmpty(phoneNumber)) {
                        setToast(view.getContext().getString(R.string.please_inform_your_phone));
                        view.mBtnSend.setEnabled(true);
                        response = false;
                        return false;
                    }

                    Pattern regex1 = Pattern.compile("[$#/.]");
                    Matcher matcher1 = regex1.matcher(phoneNumber);
                    Pattern regex = Pattern.compile("\\[|\\]");
                    Matcher matcher = regex.matcher(phoneNumber);
                    if (matcher.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }

                    mPhoneNumber = phoneNumber;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, mMessageSendPhoneNumber))
                //.observeOn(Schedulers.io())
                .doOnNext(__ -> model.setUpVerificationCallbacksSendCode(view.mBtnResend, view.mBtnSend, view.mBtnSendVerificationCode,
                        view.mTvInfoVerificationCode, view.mEtVerificationCode, mPhoneNumber,
                        mVerificationCallbacks, progress))
                .observeOn(AndroidSchedulers.mainThread())
                //.doOnEach(__ -> showLoading(false, null))
                //.retry()
                .subscribe();
    }

    private Subscription observeBtnResend(){
        return view.observableBtnResend()
                .doOnNext(__ -> view.mBtnResend.setEnabled(false))
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        view.mBtnResend.setEnabled(true);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(__ -> getPhoneNumber())
                .filter(phoneNumber -> {
                    if (TextUtils.isEmpty(phoneNumber)) {
                        setToast(view.getContext().getString(R.string.please_inform_your_phone));
                        view.mBtnResend.setEnabled(true);
                        response = false;
                        return false;
                    }

                    Pattern regex1 = Pattern.compile("[$#/.]");
                    Matcher matcher1 = regex1.matcher(phoneNumber);
                    Pattern regex = Pattern.compile("\\[|\\]");
                    Matcher matcher = regex.matcher(phoneNumber);
                    if (matcher.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }

                    mPhoneNumber = phoneNumber;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, mMessageSendPhoneNumber))
                //.observeOn(Schedulers.io())
                .doOnNext(__ -> model.setUpVerificationCallbacksResendCode(view.mBtnResend, view.mBtnSend, view.mBtnSendVerificationCode,
                        view.mTvInfoVerificationCode, view.mEtVerificationCode, mPhoneNumber,
                        mVerificationCallbacks, progress))
                //.observeOn(AndroidSchedulers.mainThread())
                //.doOnEach(__ -> showLoading(false, null))
                //.retry()
                .subscribe();
    }

    @NonNull
    private String getPhoneNumber() {
        view.mEtPhoneNumber.clearFocus();
        String phoneNumber = view.mEtPhoneNumber.getText().toString().trim();
        return "+55" + phoneNumber;
    }

    @NonNull
    private String getCode() {
        view.mEtVerificationCode.clearFocus();
        return view.mEtVerificationCode.getText().toString().trim();
    }

    //SHOW TOAST
    private void setToast(String message) {
        Toast.makeText(view.mContext, message, Toast.LENGTH_SHORT).show();
    }


    //SUBSCRIPTIONS
    private Subscription observeBtnSendVerificationCode(){
        return view.observableBtnSendVerificationCode()
                .doOnNext(__ -> view.mBtnSendVerificationCode.setEnabled(false))
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        view.mBtnSendVerificationCode.setEnabled(true);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .filter(__ -> true)
                .map(__ -> getCode())
                .filter(code -> {
                    if (TextUtils.isEmpty(code)) {
                        setToast(view.getContext().getString(R.string.please_inform_your_code));
                        view.mBtnSendVerificationCode.setEnabled(true);
                        response = false;
                        return false;
                    }

                    Pattern regex1 = Pattern.compile("[$#/.]");
                    Matcher matcher1 = regex1.matcher(code);
                    Pattern regex = Pattern.compile("\\[|\\]");
                    Matcher matcher = regex.matcher(code);
                    if (matcher.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }

                    mCode = code;
                    return true;
                })
                .filter(__ -> true)
                .doOnNext(__ -> showLoading(true, mMessageSendCode))
                .map(__ -> getUser())
                .observeOn(Schedulers.io())
                .filter(user -> {
                    if(!model.createUser(user, mCode, progress, view.mBtnSendVerificationCode)){
                        view.observableBtnSendVerificationCode().retry();
                        showLoading(false, null);
                        showMessage("Houve um erro ao cadastrar o usuário");
                        response = false;
                    }
                    else {
                        response = true;
                    }
                    return response;
                })
                .filter(__ -> true)
                .debounce(10000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if(mAuth.getCurrentUser() != null) {
                                showLoading(false, null);
                                showMessage("Usuário cadastrado com sucesso!");
                                model.startMainActivity();
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(
                        __ -> view.mContext.finish(),
                        Throwable::printStackTrace);
    }


    private User getUser(){
        User user = new User();
        user.setUsername(view.mUsernameUser);
        user.setName(view.mNameUser);
        String phoneNumber = "+55" + view.mEtPhoneNumber.getText().toString().trim();
        user.setPhoneNumber(phoneNumber);
        if(view.mImageUserUri == null){
            Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + view.getResources().getResourcePackageName(R.mipmap.ic_user_default_web)
                    + '/' + view.getResources().getResourceTypeName(R.mipmap.ic_user_default_web)
                    + '/' + view.getResources().getResourceEntryName(R.mipmap.ic_user_default_web));

            user.setImageUrl(imageUri.toString());
        }
        else {
            user.setImageUrl(String.valueOf(view.mImageUserUri));
        }

        //TIMESTAMP - FIREBASE
        Date date = new Date(System.currentTimeMillis());
        user.setDate(date);
        user.setOnline(true);
        user.setOnlineInChat(false);
        user.setAccepted(true);
        return user;
    }

    //SHOW MESSAGE AND LOADING
    private void showMessage(String message){
        Toast.makeText(view.mContext, message, Toast.LENGTH_LONG).show();
    }

    private void showLoading(boolean loading, String message){
        progress.setMessage(message);

        if(loading){
            progress.show();
        }
        else {
            progress.dismiss();
        }
    }

    public void onDestroy(){
        subscription.clear();
    }
}
