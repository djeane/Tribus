package apptribus.com.tribus.activities.check_username.mvp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


@SuppressWarnings("Convert2MethodRef")
public class CheckUsernamePresenter{

    private final CheckUsernameView view;
    private final CheckUsernameModel model;
    private final CompositeSubscription subscription = new CompositeSubscription();
    private boolean response = true;
    private ProgressDialog progress;
    private String mName;
    private String mUsername;
    private FirebaseAuth mAuth;

    public CheckUsernamePresenter(CheckUsernameView view, CheckUsernameModel model) {
        this.view = view;
        this.model = model;
        mAuth = FirebaseAuth.getInstance();
    }


    public void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openMainActivity();
        }
        else {

            subscription.add(observeButtonCheckUsername());
            subscription.add(observeEtUserName());
            subscription.add(observeIvHelp());
            subscription.add(observeTvInfoPrivacyPolicy());

            setTvInfo();


            //SET PROGRESS BAR
            progress = new ProgressDialog(view.getContext());
            progress.setMessage(view.getContext().getString(R.string.checking_username));
            progress.setCancelable(false);

        }
    }

    private void setTvInfo() {
        String textInfo = view.mContext.getResources().getString(R.string.privacy_policy_info); //index: 46 a 79

        SpannableString styledString = new SpannableString(textInfo);
        styledString.setSpan(
                new ForegroundColorSpan(view.mContext.getResources().getColor(R.color.colorAccent)),
                46,
                79,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        view.mTvInfoPrivacyPolicy.setText(styledString);

    }

    public void onResume(){
        view.mToolbarCheckUsername.setTitle(R.string.title_check_username);
        view.mToolbarCheckUsername.setTitleTextColor(view.mContext.getResources().getColor(R.color.colorIcons));

    }

    private void openMainActivity(){
        Intent intent = new Intent(view.mContext, MainActivity.class);
        view.mContext.startActivity(intent);
        view.mContext.finish();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (TextUtils.isEmpty(view.mEtUsername.getText().toString())) {
                String username = view.mTvUsername.getText().toString().trim();
                view.mTvUsername.setText(username);
            } else {
                String arroba = view.getContext().getResources().getString(R.string.arroba);
                String username = arroba + view.mEtUsername.getText().toString().trim();
                CharSequence intermediateUsername = username.replace(" ", "").toLowerCase();
                String finalUsername = Normalizer.normalize(intermediateUsername, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                view.mTvUsername.setText(finalUsername);
            }

        }
    }


    //SUBSCRIPTIONS
    private Subscription observeEtUserName() {
        return view.observeEdittext()
                .subscribe(__ ->
                        view.mEtUsername.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                //REPLACE ANY EMPTY SPACES
                                CharSequence t = view.getContext().getString(R.string.arroba) + s.toString().replace(" ", "").trim().toLowerCase();
                                String username = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                view.mTvUsername.setText(username);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                observeEditText();
                            }
                        }),
                        Throwable::printStackTrace
                );
    }


    private Subscription observeButtonCheckUsername() {
        return view.observeButton()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .map(name -> getName())
                .filter(name -> {
                    if (TextUtils.isEmpty(name)) {
                        setToast(view.getContext().getString(R.string.please_inform_your_name));
                        response = false;
                        return false;
                    }

                    Pattern regex1 = Pattern.compile("[$#/.]");
                    Matcher matcher1 = regex1.matcher(name);
                    Pattern regex = Pattern.compile("\\[|\\]");
                    Matcher matcher = regex.matcher(name);
                    if (matcher.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }

                    //STORE NAME TO SAVE IT IN SHARED PREFERENCES
                    mName = name;
                    return true;
                })
                .doOnNext(__ -> showLoading(true))
                .observeOn(Schedulers.io())
                .switchMap(username-> {

                    //STORE USERNAME TO SAVE IT IN SHARED PREFERENCES
                    mUsername = getUsername();
                    return model.verifyUsername(mUsername);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(__ -> showLoading(false))
                .retry()
                .subscribe(username -> {
                    if (username == null) {
                        model.salveNomeSharedPreference(mName);
                        model.salveUsernameSharedPreference(mUsername);
                        model.startRegisterUserActivity();
                    }
                    else {
                        showLoading(false);
                        setToast(view.getContext().getString(R.string.user_already_exist));

                    }
                },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeIvHelp(){
        return view.observeIvHelp()
                .subscribe(__ -> {
                    openDialogHelp();
                });
    }


    private void openDialogHelp() {

            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
            builder.setTitle(view.mContext.getResources().getString(R.string.about_username));
            builder.setMessage(view.getContext().getString(R.string.tv_info_check_username));


            String positiveText = view.mContext.getResources().getString(R.string.ok);
            builder.setPositiveButton(positiveText, (dialog, which) -> {
                dialog.dismiss();

            });


            AlertDialog dialog = builder.create();
            dialog.show();


    }

    //OBSERVE WHEN EDITEXT IS EMPTY - SHOWS DEFAULT STRING INSIDE TEXTVIEW
    private void observeEditText() {
        if (TextUtils.isEmpty(view.mEtUsername.getText().toString())) {
            view.mTvUsername.setText(view.getContext().getResources().getString(R.string.tv_check_username));
        }
    }

    //GET USERNAME
    @NonNull
    private String getUsername() {
        return view.mTvUsername.getText().toString().trim();
    }

    //GET NAME
    @NonNull
    private String getName() {
        view.mEtUsername.clearFocus();
        return view.mEtUsername.getText().toString().trim();
    }

    //SHOW TOAST
    private void setToast(String message) {
        Toast.makeText(view.mContext, message, Toast.LENGTH_SHORT).show();
    }

    //SHOW LOADING
    private void showLoading(boolean loading) {
        if (loading) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    private Subscription observeTvInfoPrivacyPolicy(){
        return view.observeTvInfoPrivacyPolicy()
                .subscribe(__ -> {
                    model.openPrivacyPolicyActivity();
                },
                        Throwable::printStackTrace
                );
    }


    public void onDestroy() {
        subscription.clear();

    }
}
