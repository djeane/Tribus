package apptribus.com.tribus.activities.user_profile.mvp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.user_profile.adapter.UserProfileUpdatesAdapter;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.UserUpdate;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;

/**
 * Created by User on 6/30/2017.
 */

public class UserProfilePresenter implements ToggleButton.OnCheckedChangeListener {


    private final UserProfileView view;
    private final UserProfileModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private User mUserPresenter;
    private boolean response = true;
    private ProgressDialog progress;
    private String mUsername;
    private String mName;
    private String mAboutMe;
    public static boolean isOpen;
    private int mYear, mMonth, mDay;
    private Button mButtonEditName, mButtonEditUsername;
    private EditText mEtName, mEtUsername;
    private List<UserUpdate> mListUpdates; //= new ArrayList<>();
    private UserProfileUpdatesAdapter mUserProfileUpdatesAdapter;

    //CHANGE AGE
    private DatePickerDialog.OnDateSetListener mDateSetListenter;


    public UserProfilePresenter(UserProfileView view, UserProfileModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        view.mToggleButton.setOnCheckedChangeListener(this);

        if (mListUpdates == null){
            mListUpdates = new ArrayList<>();
        }

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(getAllUpdates());
        subscription.add(observeBtnUpdate());
        subscription.add(observeBtnProfile());
        subscription.add(getAdminsNumber());
        subscription.add(observeUser());
        subscription.add(observeNumContacts());
        subscription.add(observeNumTribus());
        subscription.add(observeProfileImage());
        subscription.add(observeBtnChangeName());
        //subscription.add(observeBtnSaveChangedName());
        subscription.add(observeBtnChangeUsername());
        //subscription.add(observeEtUserName());
        //subscription.add(observeBtnSaveChangedUserName());
        subscription.add(observeBtnChangeAboutMe());
        subscription.add(observeBtnSaveChangedAboutMe());
        subscription.add(observeBtnChangeAge());
        subscription.add(observeBtnChangedAge());
        subscription.add(observeBtnChangeGender());
        subscription.add(observeBtnSaveChangedGender());
        subscription.add(observeIbArrowBack());
        subscription.add(observeIvChangeImageProfile());
        subscription.add(observeFrameProfile());
        subscription.add(observeIvHelp());

        loadMore();

        //SET PROGRESS BAR
        progress = new ProgressDialog(view.getContext());
        isOpen = true;
    }

    private Subscription getAllUpdates() {
        mListUpdates.clear();
        return model.getAllUpdates(mListUpdates)
                .subscribe(tribus -> {
                            //view.mCoordinatorUpdates.setVisibility(VISIBLE);
                            //view.mCoordinatorUpdates.bringToFront();

                            if (tribus != null && !tribus.isEmpty()) {
                                mListUpdates = tribus;
                                mUserProfileUpdatesAdapter = new UserProfileUpdatesAdapter(view.mContext, mListUpdates, view, this);
                                view.mRvUpdate.setAdapter(mUserProfileUpdatesAdapter);
                                view.mRvUpdate.setVisibility(VISIBLE);
                                view.mTvNoUpdates.setVisibility(GONE);

                            } else {
                                view.mRvUpdate.setVisibility(GONE);
                                view.mTvNoUpdates.setVisibility(VISIBLE);
                            }

                            view.mProgressBar.setVisibility(GONE);
                        },
                        Throwable::printStackTrace);

    }


    private void loadMore() {
        view.mRvUpdate.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                if (reachedBottom) {
                    view.mProgressBarBottom.bringToFront();
                    view.mProgressBarBottom.setVisibility(VISIBLE);

                    model.loadMoreUpdates(mListUpdates, mUserProfileUpdatesAdapter, view.mProgressBarBottom);
                }
            }
        });

    }

    private Subscription observeBtnUpdate(){
        return view.observableBtnUpdate()
                .subscribe(__ -> {
                    view.mBtnUpdate.setBackground(view.mContext.getResources().getDrawable(R.drawable.button_options_talker_update_pressed));
                    view.mBtnUpdate.setTextColor(view.mContext.getResources().getColor(R.color.accent));

                    view.mBtnProfile.setBackground(view.mContext.getResources().getDrawable(R.drawable.button_options_talker_profile));
                    view.mBtnProfile.setTextColor(view.mContext.getResources().getColor(R.color.primary_text));

                    view.mCoordinatorUpdates.bringToFront();
                    view.mCoordinatorUpdates.setVisibility(VISIBLE);
                    view.mRelativeProfile.setVisibility(GONE);

                });
    }

    private Subscription observeBtnProfile(){
        return view.observableBtnProfile()
                .subscribe(__ -> {
                    view.mBtnProfile.setBackground(view.mContext.getResources().getDrawable(R.drawable.button_options_talker_profile_pressed));
                    view.mBtnProfile.setTextColor(view.mContext.getResources().getColor(R.color.accent));

                    view.mBtnUpdate.setBackground(view.mContext.getResources().getDrawable(R.drawable.button_options_talker_update));
                    view.mBtnUpdate.setTextColor(view.mContext.getResources().getColor(R.color.primary_text));

                    view.mRelativeProfile.bringToFront();
                    view.mRelativeProfile.setVisibility(VISIBLE);
                    view.mCoordinatorUpdates.setVisibility(GONE);

                });
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();
        mDateSetListenter = (view1, year, month, dayOfMonth) -> {

            mYear = year;
            mMonth = month;
            mDay = dayOfMonth;

            getAge(year, month, dayOfMonth);
        };

        //SET SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.mContext,
                R.array.gender_update, android.R.layout.simple_spinner_dropdown_item);
        view.mSpGender.setAdapter(adapter);

    }

    public void onStop() {
        isOpen = false;
    }

    private Subscription getAdminsNumber(){
        return model.getAdminsNumber(new ArrayList<>())
                .subscribe(tribus -> {
                    String appendNumAdmin;
                    if (tribus != null){

                        if (tribus.size() == 0){
                            appendNumAdmin = "Não sou admin ainda.";
                            view.mTvNumAdmin.setText(appendNumAdmin);
                        }
                        else if(tribus.size() == 1){
                            appendNumAdmin = "Sou admin de 1 tribu.";
                            view.mTvNumAdmin.setText(appendNumAdmin);
                        }
                        else {
                            appendNumAdmin = "Sou admin de " +  tribus.size() + " tribus.";
                            view.mTvNumAdmin.setText(appendNumAdmin);
                        }
                    }
                    else {
                        appendNumAdmin = "Não sou admin ainda.";
                        view.mTvNumAdmin.setText(appendNumAdmin);
                    }

                });
    }

    //SUBSCRIPTIONS
    private Subscription observeNumContacts() {
        return model.getNumContacts()
                .subscribe(this::setNumContacts,
                        Throwable::printStackTrace
                );
    }

    private Subscription observeNumTribus() {
        return model.getNumTribus()
                .subscribe(this::setNumTribus,
                        Throwable::printStackTrace
                );
    }

    private void setNumContacts(int numContacts) {
        String appendNumContacts;

        if (numContacts == 0 || numContacts == 1) {
            appendNumContacts = String.valueOf(numContacts) + " contato";
        } else {
            appendNumContacts = String.valueOf(numContacts) + " contatos";
        }

        view.mTvNumContacts.setText(appendNumContacts);
    }

    private void setNumTribus(int numTribus) {
        String appendNumTribus;

        if (numTribus == 0 || numTribus == 1) {
            appendNumTribus = "participa de " + String.valueOf(numTribus) + " mTribu";
        } else {
            appendNumTribus = "participa de " + String.valueOf(numTribus) + " tribus";
        }

        view.mTvNumTribus.setText(appendNumTribus);
    }



    private Subscription observeProfileImage(){
        return view.observeProfileImage()
                .subscribe(__ -> {
                            if (mUserPresenter != null){
                                model.openShowProfileImageActivity(mUserPresenter.getImageUrl());
                            }
                        },
                        Throwable::printStackTrace
                );
    }


    //observableIbArrowBack
    private Subscription observeIbArrowBack() {
        return view.observableIbArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }


    private Subscription observeUser() {
        return model.getUser()
                .subscribe(user -> {
                            mUserPresenter = user;
                            setImage(user);
                            setFields(user);
                            setToggleButton(user);


                        },
                        Throwable::printStackTrace
                );
    }


    //TO CHANGE NAME
    private Subscription observeBtnChangeName() {
        return view.observableBtnChangeName()
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
                .subscribe(__ -> {

                    if (mUserPresenter != null){
                        openDialogEditName(mUserPresenter);
                    }
                        },
                        Throwable::printStackTrace
                );
    }

    private void openDialogEditName(User user) {

        String oldName = user.getName();

            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
            LayoutInflater inflater = view.mContext.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_name, null);

            mEtName = dialogView.findViewById(R.id.et_name);
            mEtName.setText(oldName);
            mEtName.setSelection(mEtName.getText().length());
            TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
            Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
            mButtonEditName = dialogView.findViewById(R.id.btn_edit_name);

            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            dialog.getWindow().setGravity(wmlp.gravity);

            subscription.add(observeBtnSaveChangedName(dialog));

            dialog.show();

            mEtName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String appendCount = String.valueOf(s.length()) + "/40";
                    mTvCharacterCount.setText(appendCount);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });


            mBtnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
    }

    private void openDialogEditUsername(User user) {

        CharSequence currentUsername = user.getUsername().trim().replace("@", "").toLowerCase();

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_username, null);

        mEtUsername = dialogView.findViewById(R.id.et_username);
        mEtUsername.setText(currentUsername);
        mEtUsername.setSelection(mEtUsername.getText().length());
        TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
        Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
        mButtonEditUsername = dialogView.findViewById(R.id.btn_edit_username);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        dialog.getWindow().setGravity(wmlp.gravity);

        subscription.add(observeBtnSaveChangedUsername(dialog));

        dialog.show();

        mEtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String appendCount = String.valueOf(s.length()) + "/40";
                mTvCharacterCount.setText(appendCount);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        mBtnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private Observable<Void> observableBtnSaveChangedName(){
        return RxView.clicks(mButtonEditName);
    }

    private Subscription observeBtnSaveChangedName(AlertDialog dialog){
        return observableBtnSaveChangedName()
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
                        //view.mEtProfileUserName.setVisibility(GONE);
                        view.mTvProfileUserName.setVisibility(VISIBLE);
                        //view.mBtnSaveChangedName.setVisibility(GONE);
                        view.mBtnChangeName.setVisibility(VISIBLE);
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
                    }
                    else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }


                    //STORE NAME TO SAVE IT IN SHARED PREFERENCES
                    mName = name;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu nome é atualizado..."))
                //.observeOn(Schedulers.io())
                .doOnNext(__ -> model.updateName(mName, dialog))
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .doOnEach(__ -> showLoading(false, null))
                .subscribe(newName -> {
                            //showLoading(false, null);
                            //setToast("Nome atualizado com sucesso!");
                            //view.mEtProfileUserName.setVisibility(GONE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            //view.mBtnSaveChangedName.setVisibility(GONE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            dialog.dismiss();
                        },
                        Throwable::printStackTrace
                );

    }

    private Observable<Void> observableBtnSaveChangedUsername(){
        return RxView.clicks(mButtonEditUsername);
    }

    private Subscription observeBtnSaveChangedUsername(AlertDialog dialog){
        return observableBtnSaveChangedUsername()
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
                .map(name -> getUsername())
                .filter(name -> {
                    if (TextUtils.isEmpty(name)) {
                        setToast("Por favor, informe um nome de usuário");
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

                    mUsername = name;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu nome de usuário é atualizado..."))
                //.observeOn(Schedulers.io())
                .switchMap(model::verifyUsername)
                //.observeOn(AndroidSchedulers.mainThread())
                .doOnEach(__ -> showLoading(false, null))
                //.retry()
                .subscribe(username -> {
                            if (username == null) {
                                //model.startUserRegisterActivity();
                                //view.mEtProfileUserUsername.setVisibility(GONE);
                                view.mTvProfileUserUsername.setVisibility(VISIBLE);
                                //view.mBtnSaveChangedUsername.setVisibility(GONE);
                                view.mBtnChangeUsername.setVisibility(VISIBLE);
                                model.updateUsername(mUsername, dialog);

                            } else {
                                showLoading(false, null);
                                setToast("O nome de usuário já existe!");
                                //view.mEtProfileUserUsername.setVisibility(GONE);
                                view.mTvProfileUserUsername.setVisibility(VISIBLE);
                                //view.mBtnSaveChangedUsername.setVisibility(GONE);
                                view.mBtnChangeUsername.setVisibility(VISIBLE);
                                view.mTvProfileUserUsername.setText(mUserPresenter.getUsername().trim());
                                dialog.dismiss();
                            }
                        },
                        Throwable::printStackTrace
                );

    }


    //TO CHANGE USERNAME
    private Subscription observeBtnChangeUsername() {
        return view.observableBtnChangeUsername()
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
                .subscribe(__ -> {

                    if (mUserPresenter != null){

                        openDialogEditUsername(mUserPresenter);
                    }

                        },
                        Throwable::printStackTrace
                );
    }


    /*public Observable<CharSequence> observeEtUserUsername() {
        return RxTextView.textChanges(mEtUsername);
    }

    private Subscription observeEtUserName() {
        return observeEtUserUsername()
                .subscribe(__ ->
                                mEtUsername.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        CharSequence t = "@" + s.toString().replace(" ", "").toLowerCase();
                                        view.mTvProfileUserUsername.setText(t);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                }),
                        Throwable::printStackTrace
                );
    }*/


    //TO CHANGE ABOUT ME
    private Subscription observeBtnChangeAboutMe() {
        return view.observableBtnChangeAboutMe()
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
                .subscribe(__ -> {
                            view.mTvAboutMe.setVisibility(GONE);
                            view.mEtAboutMe.setVisibility(VISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(GONE);
                            view.mBtnSaveChangedAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setSelection(view.mEtAboutMe.getText().length());

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //view.mEtProfileUserName.setVisibility(INVISIBLE);
                            //view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);


                            if (mUserPresenter != null) {
                                view.mEtAboutMe.setText(mUserPresenter.getAboutMe());
                            }

                            View viewFocus = model.activity.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedAboutMe() {
        return view.observableBtnSaveChangedAboutMe()
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
                .map(aboutMe -> getAboutMe())
                .doOnNext(aboutMe -> {
                    mAboutMe = aboutMe;
                    showLoading(true, "Aguarde enquanto seu status é atualizado...");
                })
                //.observeOn(Schedulers.io())
                .doOnNext(model::updateAboutMe)
                //.observeOn(AndroidSchedulers.mainThread())
                //.doOnEach(__ -> showLoading(false))
                //.retry()
                .subscribe(aboutMe -> {
                            showLoading(false, null);
                            setToast("Status atualizado com sucesso!");
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);

                        },
                        Throwable::printStackTrace);

    }


    //TO CHANGE AGE
    private Subscription observeBtnChangeAge() {
        return view.observableBtnChangeAge()
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
                .subscribe(__ -> {
                            showCalendar();
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //view.mEtProfileUserName.setVisibility(INVISIBLE);
                            //view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);

                            view.mBtnChangeAge.setVisibility(GONE);
                            view.mBtnSaveChangedAge.setVisibility(VISIBLE);
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnChangedAge() {
        return view.observableBtnSaveChangedAge()
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
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto sua idade é atualizada..."))
                //.observeOn(Schedulers.io())
                .doOnNext(__ -> model.updateAge(mYear, mMonth, mDay))
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .subscribe(__ -> {
                            showLoading(false, null);
                            setToast("Idade atualizada com sucesso!");
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                        },
                        Throwable::printStackTrace
                );
    }


    //TO GCHANGE GENDER
    private Subscription observeBtnChangeGender() {
        return view.observableBtnChangeGender()
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
                .subscribe(__ -> {
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //view.mEtProfileUserName.setVisibility(INVISIBLE);
                            //view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                            view.mSpGender.setVisibility(VISIBLE);
                            view.mTvGender.setVisibility(GONE);
                            view.mBtnChangeGender.setVisibility(GONE);
                            view.mBtnSaveChangedGender.setVisibility(VISIBLE);

                            View viewFocus = model.activity.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedGender(){
        return view.observableBtnSaveChangedGender()
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
                .map(__ -> getGender())
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu gênero é atualizado..."))
                //.observeOn(Schedulers.io())
                .doOnNext(model::updateGender)
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .subscribe(__ -> {
                            showLoading(false, null);
                            setToast("Gênero atualizado com sucesso!");
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);

                        },
                        Throwable::printStackTrace
                );
    }

    private void getAge(int year, int month, int dayOfMonth) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if (month > currentMonth) {
            --age;
        } else if (month == currentMonth) {
            if (dayOfMonth > todayDay) {
                --age;
            }
        }

        if(age <= 0){
            view.mTvAge.setText("");
            view.mTvAge.setHint("Informe sua data de nascimento...");
            view.mBtnChangeAge.setVisibility(VISIBLE);
            view.mBtnSaveChangedAge.setVisibility(GONE);
        }
        else if(age == 1) {
            String appendAge = "Tenho " + age + " ano";
            view.mTvAge.setText("");
            view.mTvAge.setHint(appendAge);

        }
        else {
            String appendAge = "Tenho " + age + " anos";
            view.mTvAge.setText("");
            view.mTvAge.setHint(appendAge);
        }

        /*String appendAge = "Tenho " + age + " anos";
        view.mTvAge.setText("");
        view.mTvAge.setHint(appendAge);*/
    }

    private void showAge(User user) {
        int age;
        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        if (user.getYear() != 0) {

            age = currentYear - user.getYear();

            if (user.getMonth() > currentMonth) {
                --age;
            } else if (user.getMonth() == currentMonth) {
                if (user.getDay() > todayDay) {
                    --age;
                }
            }

            if(age <= 0){
                view.mTvAge.setText("");
                view.mTvAge.setHint("Informe sua data de nascimento...");
            }
            else if(age == 1) {
                String appendAge = "Tenho " + age + " ano";
                view.mTvAge.setHint("");
                view.mTvAge.setText(appendAge);
            }
            else {
                String appendAge = "Tenho " + age + " anos";
                view.mTvAge.setHint("");
                view.mTvAge.setText(appendAge);
            }


        }
        else {
            view.mTvAge.setText("");
            view.mTvAge.setHint("Informe sua data de nascimento...");
        }
    }


    private void showCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                view.mContext,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListenter,
                year, month, day
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    //GET USERNAME
    @NonNull
    private String getUsername() {
        String username = mEtUsername.getText().toString().trim();
        String newUsername = Normalizer.normalize(username, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        if (newUsername.equals("")) {
            return newUsername;
        } else {
            return "@" + newUsername.replace(" ", "").toLowerCase();
        }
    }

    //GET NAME
    private String getName() {
        String name = mEtName.getText().toString().trim();
        //String newName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        if (!name.equals("")) {
            return name;
        }
        return "";

    }

    //GET NAME
    private String getAboutMe() {
        return view.mEtAboutMe.getText().toString().trim();
        /*8if (!aboutMe.equals("")) {
            return aboutMe;
        }
        return "";*/

    }

    //GET GENDER
    //GET NAME
    private String getGender() {

        return view.mSpGender.getSelectedItem().toString().trim();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            /*if (TextUtils.isEmpty(view.mEtProfileUserUsername.getText().toString())) {
                String username = view.mTvProfileUserUsername.getText().toString();
                view.mTvProfileUserUsername.setText(username);
            } else {
                String arroba = "@";
                String username = arroba + view.mEtProfileUserUsername.getText().toString();
                view.mTvProfileUserUsername.setText(username);
            }*/


        }
    }


    //SHOW TOAST
    private void setToast(String message) {
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }


    //SHOW LOADING
    private void showLoading(boolean loading, String message) {

        progress.setMessage(message);
        progress.setCancelable(false);

        if (loading) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private Subscription observeIvChangeImageProfile() {
        return view.observableIvChangeImageProfile()
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
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                .doOnNext(__ -> showDialogToUpdateImage())
                //.doOnNext(__ -> model.getImage()) //GET IMAGE STRAIGHT IF THERE IS PERMISSION ALREADY
                .subscribe(__ -> {
                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);

                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //view.mEtProfileUserName.setVisibility(INVISIBLE);
                            //view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            //view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeFrameProfile() {
        return view.observeFrameProfile()
                .subscribe(__ -> {

                            //aboutMe
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);

                            //name
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //username
                            //view.mEtProfileUserName.setVisibility(GONE);
                            //view.mEtProfileUserUsername.setVisibility(GONE);
                            //view.mBtnSaveChangedName.setVisibility(GONE);
                            //view.mBtnSaveChangedUsername.setVisibility(GONE);

                            //age
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                            showAge(mUserPresenter);

                            //gender
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);

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
        builder.setTitle("Sobre sua permissão...");
        builder.setMessage(view.getContext().getString(R.string.tv_info_permission));


        String positiveText = "OK, ENTENDI!";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });


        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void showDialogToUpdateImage() {

        PickSetup setup = new PickSetup()
                .setTitle("Escolha")
                .setTitleColor(view.getResources().getColor(R.color.primary_text))
                .setBackgroundColor(Color.WHITE)
                .setCancelText("Cancelar")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText("Câmera")
                .setGalleryButtonText("Galeria de Imagens")
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(false)
                .setCameraIcon(R.mipmap.camera_colored)
                .setGalleryIcon(R.mipmap.gallery_colored);

        PickImageDialog.build(setup).show(view.mContext);
    }

    //SET NEW IMAGE
    public void setImageUser(Uri image) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_profile_image, null);
        builder.setView(dialogView);
        SimpleDraweeView mSdChangePhoto = dialogView.findViewById(R.id.sd_large_image_user);
        AppCompatButton mBtnChangePhoto = dialogView.findViewById(R.id.btn_change_photo);
        AppCompatButton mBtnCancel = dialogView.findViewById(R.id.btn_cancel);

        builder.setMessage("Mudar foto de perfil?");

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(image)
                .setControllerListener(listener)
                .setOldController(mSdChangePhoto.getController())
                .build();
        mSdChangePhoto.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.show();

        //CLICK LISTENER TO BUTTONS
        mBtnChangePhoto.setOnClickListener(v -> {
            //CHECK INTERNET CONNECTION
            if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            }
            else {
                ShowSnackBarInfoInternet.showSnack(true, view);
                model.uploadImageToFirebase(image);
                dialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }


    //GET IMAGE AFTER GRANTED PERMISSION
    /*public void getImage() {
        model.getImage();
    }*/


    //SEND IMAGE TO UPDATE
    public void uploadImageToFirebase(Uri image) {
        model.uploadImageToFirebase(image);
    }


    //SETTING VIEWS
    //mLargeUserImage
    private void setImage(User user) {
        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };

        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(user.getImageUrl()))
                .setControllerListener(listener)
                .setOldController(view.mCircleImageUser.getController())
                .build();
        view.mCircleImageUser.setController(dc);

    }

    private void setFields(User user) {
        //String date = "Perfil criado " + GetTimeAgo.getTimeAgo(user.getDate(), view.getContext());
        //view.mTvDate.setText(date);
        view.mTvProfileUserName.setText(user.getName());
        view.mTvProfileUserUsername.setText(user.getUsername());

        if (user.getAboutMe() != null || !user.getAboutMe().equals("")) {
            view.mTvAboutMe.setText(user.getAboutMe());
        }
        else if(user.getAboutMe().equals("")){
            view.mTvAboutMe.setHint(view.getResources().getString(R.string.hint_about_me));
        }

        if (mUserPresenter.getYear() != 0) {
            showAge(user);
        } else {
            view.mTvAge.setText("");
            view.mTvAge.setHint(view.getResources().getString(R.string.hint_age));
        }

        if(mUserPresenter.getGender() != null){
            view.mTvGender.setText(user.getGender());
        }
        else {
            view.mTvGender.setHint(view.getResources().getString(R.string.hint_gender));
        }

    }

    private void setToggleButton(User user) {

        /*if (user.isAccepted()) {
            view.mToggleButton.setText("SIM");
            view.mToggleButton.setChecked(true);
        } else {
            view.mToggleButton.setText("NÃO");
            view.mToggleButton.setChecked(false);
        }*/

        view.mToggleButton.setChecked(user.isAccepted());
    }

    //TOGGLE BUTTON
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //CHECK INTERNET CONNECTION
        if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
            ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
        }
        else {
            ShowSnackBarInfoInternet.showSnack(true, view);
            //TO HANDLE CHANGED ORIENTATION
            if (mUserPresenter != null) {
                if (isChecked == mUserPresenter.isAccepted()) {
                    return;

                } else {
                    //model.updatePermissions(!mUserPresenter.isAccepted(), buttonView);
                    model.updatePermissions(isChecked, buttonView);
                    //}

                }
            }
        }

    }


    public void onDestroy() {
        subscription.clear();
    }




























    //OLD CODE
    /*private final UserProfileView view;
    private final UserProfileModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private User mUserPresenter;
    private boolean response = true;
    private ProgressDialog progress;
    private String mUsername;
    private String mName;
    private String mAboutMe;
    public static boolean isOpen;
    private int mYear, mMonth, mDay;

    //CHANGE AGE
    private DatePickerDialog.OnDateSetListener mDateSetListenter;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceUserName = mDatabase.getReference().child(INDIVIDUAL_USERS);


    public UserProfilePresenter(UserProfileView view, UserProfileModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        view.mToggleButton.setOnCheckedChangeListener(this);

        //KEEP SYNCED
        mReferenceUsers.keepSynced(true);
        mReferenceUserName.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeUser());
        subscription.add(observeProfileImage());
        subscription.add(observeBtnChangeName());
        subscription.add(observeBtnSaveChangedName());
        subscription.add(observeBtnChangeUsername());
        subscription.add(observeEtUserName());
        subscription.add(observeBtnSaveChangedUserName());
        subscription.add(observeBtnChangeAboutMe());
        subscription.add(observeBtnSaveChangedAboutMe());
        subscription.add(observeBtnChangeAge());
        subscription.add(observeBtnChangedAge());
        subscription.add(observeBtnChangeGender());
        subscription.add(observeBtnSaveChangedGender());
        subscription.add(observeIbArrowBack());
        subscription.add(observeIvChangeImageProfile());
        subscription.add(observeFrameProfile());
        subscription.add(observeIvHelp());


        //SET PROGRESS BAR
        progress = new ProgressDialog(view.getContext());
        isOpen = true;
    }


    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();
        mDateSetListenter = (view1, year, month, dayOfMonth) -> {

            mYear = year;
            mMonth = month;
            mDay = dayOfMonth;

            getAge(year, month, dayOfMonth);
        };

        //SET SPINNER
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.mContext,
                R.array.gender_update, android.R.layout.simple_spinner_dropdown_item);
        view.mSpGender.setAdapter(adapter);

    }

    public void onStop() {
        model.removeListeners();
        isOpen = false;
    }

    //SUBSCRIPTIONS


    private Subscription observeProfileImage(){
        return view.observeProfileImage()
                .subscribe(__ -> {
                    if (mUserPresenter != null){
                        model.openShowProfileImageActivity(mUserPresenter.getImageUrl());
                    }
                },
                        Throwable::printStackTrace
                );
    }


    //observableIbArrowBack
    private Subscription observeIbArrowBack() {
        return view.observableIbArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace
                );
    }


    private Subscription observeUser() {
        return model.getCurrentUser()
                .subscribe(user -> {
                            mUserPresenter = user;
                            setImage(user);
                            setFields(user);
                            setToggleButton(user);


                        },
                        Throwable::printStackTrace
                );
    }


    //TO CHANGE NAME
    private Subscription observeBtnChangeName() {
        return view.observableBtnChangeName()
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
                .subscribe(__ -> {
                            view.mTvProfileUserName.setVisibility(GONE);
                            view.mEtProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(GONE);
                            view.mBtnSaveChangedName.setVisibility(VISIBLE);

                            //VISIBILITY OF ALL THE OTHERS VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            view.mEtAboutMe.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(INVISIBLE);

                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mSpGender.setVisibility(INVISIBLE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(INVISIBLE);

                            if (mUserPresenter != null) {
                                view.mEtProfileUserName.setText(mUserPresenter.getName());
                            }

                            View viewFocus = view.mContext.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) view.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedName() {
        return view.observableBtnSaveChangedName()
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
                        view.mEtProfileUserName.setVisibility(GONE);
                        view.mTvProfileUserName.setVisibility(VISIBLE);
                        view.mBtnSaveChangedName.setVisibility(GONE);
                        view.mBtnChangeName.setVisibility(VISIBLE);
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
                    }
                    else if (matcher1.find()) {
                        setToast(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }


                    //STORE NAME TO SAVE IT IN SHARED PREFERENCES
                    mName = name;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu nome é atualizado..."))
                //.observeOn(Schedulers.io())
                .doOnNext(model::updateName)
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .subscribe(newName -> {
                            showLoading(false, null);
                            setToast("Nome atualizado com sucesso!");
                            view.mEtProfileUserName.setVisibility(GONE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnSaveChangedName.setVisibility(GONE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                        },
                        Throwable::printStackTrace
                );

    }


    //TO CHANGE USERNAME
    private Subscription observeBtnChangeUsername() {
        return view.observableBtnChangeUsername()
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
                .subscribe(__ -> {
                            view.mTvProfileUserUsername.setVisibility(GONE);
                            view.mEtProfileUserUsername.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(GONE);
                            view.mBtnSaveChangedUsername.setVisibility(VISIBLE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mEtProfileUserName.setVisibility(INVISIBLE);
                            view.mEtAboutMe.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(INVISIBLE);
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);
                            view.mSpGender.setVisibility(INVISIBLE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(INVISIBLE);


                            if (mUserPresenter != null) {
                                CharSequence currentUsername = mUserPresenter.getUsername().trim().replace("@", "").toLowerCase();
                                view.mEtProfileUserUsername.setText(currentUsername);
                            }

                            View viewFocus = model.activity.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedUserName() {
        return view.observeBtnSaveChangedUsername()
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
                .map(name -> getUsername())
                .filter(name -> {
                    if (TextUtils.isEmpty(name)) {
                        setToast("Por favor, informe um nome de usuário");
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

                    mUsername = name;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu nome de usuário é atualizado..."))
                //.observeOn(Schedulers.io())
                .switchMap(model::verifyUsername)
                //.observeOn(AndroidSchedulers.mainThread())
                .doOnEach(__ -> showLoading(false, null))
                //.retry()
                .subscribe(username -> {
                            if (username == null) {
                                //model.startUserRegisterActivity();
                                view.mEtProfileUserUsername.setVisibility(GONE);
                                view.mTvProfileUserUsername.setVisibility(VISIBLE);
                                view.mBtnSaveChangedUsername.setVisibility(GONE);
                                view.mBtnChangeUsername.setVisibility(VISIBLE);
                                model.updateUsername(mUsername);
                            } else {
                                showLoading(false, null);
                                setToast("O nome de usuário já existe!");
                                view.mEtProfileUserUsername.setVisibility(GONE);
                                view.mTvProfileUserUsername.setVisibility(VISIBLE);
                                view.mBtnSaveChangedUsername.setVisibility(GONE);
                                view.mBtnChangeUsername.setVisibility(VISIBLE);
                                view.mTvProfileUserUsername.setText(mUserPresenter.getUsername().trim());

                            }
                        },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeEtUserName() {
        return view.observeEtUserUsername()
                .subscribe(__ ->
                                view.mEtProfileUserUsername.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        CharSequence t = "@" + s.toString().replace(" ", "").toLowerCase();
                                        view.mTvProfileUserUsername.setText(t);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                }),
                        Throwable::printStackTrace
                );
    }


    //TO CHANGE ABOUT ME
    private Subscription observeBtnChangeAboutMe() {
        return view.observableBtnChangeAboutMe()
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
                .subscribe(__ -> {
                            view.mTvAboutMe.setVisibility(GONE);
                            view.mEtAboutMe.setVisibility(VISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(GONE);
                            view.mBtnSaveChangedAboutMe.setVisibility(VISIBLE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            view.mEtProfileUserName.setVisibility(INVISIBLE);
                            view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mSpGender.setVisibility(INVISIBLE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(INVISIBLE);


                            if (mUserPresenter != null) {
                                view.mEtAboutMe.setText(mUserPresenter.getAboutMe());
                            }

                            View viewFocus = model.activity.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedAboutMe() {
        return view.observableBtnSaveChangedAboutMe()
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
                .map(aboutMe -> getAboutMe())
                .doOnNext(aboutMe -> {
                    mAboutMe = aboutMe;
                    showLoading(true, "Aguarde enquanto seu status é atualizado...");
                })
                //.observeOn(Schedulers.io())
                .doOnNext(model::updateAboutMe)
                //.observeOn(AndroidSchedulers.mainThread())
                //.doOnEach(__ -> showLoading(false))
                //.retry()
                .subscribe(aboutMe -> {
                            showLoading(false, null);
                            setToast("Status atualizado com sucesso!");
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);

                        },
                        Throwable::printStackTrace);

    }


    //TO CHANGE AGE
    private Subscription observeBtnChangeAge() {
        return view.observableBtnChangeAge()
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
                .subscribe(__ -> {
                            showCalendar();
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(INVISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(INVISIBLE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            view.mEtProfileUserName.setVisibility(INVISIBLE);
                            view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);

                            view.mBtnChangeAge.setVisibility(GONE);
                            view.mBtnSaveChangedAge.setVisibility(VISIBLE);
                            view.mSpGender.setVisibility(INVISIBLE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(INVISIBLE);
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnChangedAge() {
        return view.observableBtnSaveChangedAge()
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
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto sua idade é atualizada..."))
                //.observeOn(Schedulers.io())
                .doOnNext(__ -> model.updateAge(mYear, mMonth, mDay))
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .subscribe(__ -> {
                            showLoading(false, null);
                            setToast("Idade atualizada com sucesso!");
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                        },
                        Throwable::printStackTrace
                );
    }


    //TO GCHANGE GENDER
    private Subscription observeBtnChangeGender() {
        return view.observableBtnChangeGender()
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
                .subscribe(__ -> {
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(INVISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(INVISIBLE);

                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            view.mEtProfileUserName.setVisibility(INVISIBLE);
                            view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(INVISIBLE);
                            view.mSpGender.setVisibility(VISIBLE);
                            view.mTvGender.setVisibility(GONE);
                            view.mBtnChangeGender.setVisibility(GONE);
                            view.mBtnSaveChangedGender.setVisibility(VISIBLE);

                            View viewFocus = model.activity.getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) model.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSaveChangedGender(){
        return view.observableBtnSaveChangedGender()
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
                .map(__ -> getGender())
                .doOnNext(__ -> showLoading(true, "Aguarde enquanto seu gênero é atualizado..."))
                //.observeOn(Schedulers.io())
                .doOnNext(model::updateGender)
                //.observeOn(AndroidSchedulers.mainThread())
                //.retry()
                .subscribe(__ -> {
                            showLoading(false, null);
                            setToast("Gênero atualizado com sucesso!");
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);

                        },
                        Throwable::printStackTrace
                );
    }

    private void getAge(int year, int month, int dayOfMonth) {

        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if (month > currentMonth) {
            --age;
        } else if (month == currentMonth) {
            if (dayOfMonth > todayDay) {
                --age;
            }
        }

        if(age <= 0){
            view.mTvAge.setText("");
            view.mTvAge.setHint("Informe sua data de nascimento...");
            view.mBtnChangeAge.setVisibility(VISIBLE);
            view.mBtnSaveChangedAge.setVisibility(GONE);
        }
        else if(age == 1) {
            String appendAge = "Tenho " + age + " ano";
            view.mTvAge.setText("");
            view.mTvAge.setHint(appendAge);

        }
        else {
            String appendAge = "Tenho " + age + " anos";
            view.mTvAge.setText("");
            view.mTvAge.setHint(appendAge);
        }
    }

    private void showAge(User user) {
        int age;
        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        if (user.getYear() != 0) {

            age = currentYear - user.getYear();

            if (user.getMonth() > currentMonth) {
                --age;
            } else if (user.getMonth() == currentMonth) {
                if (user.getDay() > todayDay) {
                    --age;
                }
            }

            if(age <= 0){
                view.mTvAge.setText("");
                view.mTvAge.setHint("Informe sua data de nascimento...");
            }
            else if(age == 1) {
                String appendAge = "Tenho " + age + " ano";
                view.mTvAge.setHint("");
                view.mTvAge.setText(appendAge);
            }
            else {
                String appendAge = "Tenho " + age + " anos";
                view.mTvAge.setHint("");
                view.mTvAge.setText(appendAge);
            }


        }
        else {
            view.mTvAge.setText("");
            view.mTvAge.setHint("Informe sua data de nascimento...");
        }
    }


    private void showCalendar() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                view.mContext,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListenter,
                year, month, day
        );

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    //GET USERNAME
    @NonNull
    private String getUsername() {
        String username = view.mEtProfileUserUsername.getText().toString().trim();
        String newUsername = Normalizer.normalize(username, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        if (newUsername.equals("")) {
            return newUsername;
        } else {
            return "@" + newUsername.replace(" ", "").toLowerCase();
        }
    }

    //GET NAME
    private String getName() {
        String name = view.mEtProfileUserName.getText().toString().trim();
        //String newName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        if (!name.equals("")) {
            return name;
        }
        return "";

    }

    //GET NAME
    private String getAboutMe() {
        return view.mEtAboutMe.getText().toString().trim();
        /*8if (!aboutMe.equals("")) {
            return aboutMe;
        }
        return "";

    }

    //GET GENDER
    //GET NAME
    private String getGender() {

        return view.mSpGender.getSelectedItem().toString().trim();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (TextUtils.isEmpty(view.mEtProfileUserUsername.getText().toString())) {
                String username = view.mTvProfileUserUsername.getText().toString();
                view.mTvProfileUserUsername.setText(username);
            } else {
                String arroba = "@";
                String username = arroba + view.mEtProfileUserUsername.getText().toString();
                view.mTvProfileUserUsername.setText(username);
            }


        }
    }


    //SHOW TOAST
    private void setToast(String message) {
        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
    }


    //SHOW LOADING
    private void showLoading(boolean loading, String message) {

        progress.setMessage(message);
        progress.setCancelable(false);

        if (loading) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private Subscription observeIvChangeImageProfile() {
        return view.observableIvChangeImageProfile()
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
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                .doOnNext(__ -> showDialogToUpdateImage())
                //.doOnNext(__ -> model.getImage()) //GET IMAGE STRAIGHT IF THERE IS PERMISSION ALREADY
                .subscribe(__ -> {
                            //VISIBILITIES OF OTHER VIEWS
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(INVISIBLE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(INVISIBLE);

                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            view.mEtProfileUserName.setVisibility(INVISIBLE);
                            view.mEtProfileUserUsername.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedName.setVisibility(INVISIBLE);
                            view.mBtnSaveChangedUsername.setVisibility(INVISIBLE);
                            view.mTvAge.setVisibility(VISIBLE);

                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(INVISIBLE);
                            view.mSpGender.setVisibility(INVISIBLE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(INVISIBLE);

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeFrameProfile() {
        return view.observeFrameProfile()
                .subscribe(__ -> {

                            //aboutMe
                            view.mTvAboutMe.setVisibility(VISIBLE);
                            view.mEtAboutMe.setVisibility(GONE);
                            view.mBtnChangeAboutMe.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAboutMe.setVisibility(GONE);

                            //name
                            view.mTvProfileUserUsername.setVisibility(VISIBLE);
                            view.mTvProfileUserName.setVisibility(VISIBLE);
                            view.mBtnChangeName.setVisibility(VISIBLE);
                            view.mBtnChangeUsername.setVisibility(VISIBLE);

                            //username
                            view.mEtProfileUserName.setVisibility(GONE);
                            view.mEtProfileUserUsername.setVisibility(GONE);
                            view.mBtnSaveChangedName.setVisibility(GONE);
                            view.mBtnSaveChangedUsername.setVisibility(GONE);

                            //age
                            view.mBtnChangeAge.setVisibility(VISIBLE);
                            view.mBtnSaveChangedAge.setVisibility(GONE);
                            showAge(mUserPresenter);

                            //gender
                            view.mSpGender.setVisibility(GONE);
                            view.mTvGender.setVisibility(VISIBLE);
                            view.mBtnChangeGender.setVisibility(VISIBLE);
                            view.mBtnSaveChangedGender.setVisibility(GONE);

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
        builder.setTitle("Sobre sua permissão...");
        builder.setMessage(view.getContext().getString(R.string.tv_info_permission));


        String positiveText = "OK, ENTENDI!";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });


        AlertDialog dialog = builder.create();
        dialog.show();


    }


    private void showDialogToUpdateImage() {

        PickSetup setup = new PickSetup()
                .setTitle("Escolha")
                .setTitleColor(view.getResources().getColor(R.color.primary_text))
                .setBackgroundColor(Color.WHITE)
                .setCancelText("Cancelar")
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText("Câmera")
                .setGalleryButtonText("Galeria de Imagens")
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(false)
                .setCameraIcon(R.mipmap.camera_colored)
                .setGalleryIcon(R.mipmap.gallery_colored);

        PickImageDialog.build(setup).show(view.mContext);
    }

    //SET NEW IMAGE
    public void setImageUser(Uri image) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_profile_image, null);
        builder.setView(dialogView);
        SimpleDraweeView mSdChangePhoto = dialogView.findViewById(R.id.sd_large_image_user);
        AppCompatButton mBtnChangePhoto = dialogView.findViewById(R.id.btn_change_photo);
        AppCompatButton mBtnCancel = dialogView.findViewById(R.id.btn_cancel);

        builder.setMessage("Mudar foto de perfil?");

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(image)
                .setControllerListener(listener)
                .setOldController(mSdChangePhoto.getController())
                .build();
        mSdChangePhoto.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.show();

        //CLICK LISTENER TO BUTTONS
        mBtnChangePhoto.setOnClickListener(v -> {
            //CHECK INTERNET CONNECTION
            if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            }
            else {
                ShowSnackBarInfoInternet.showSnack(true, view);
                model.uploadImageToFirebase(image);
                dialog.dismiss();
            }
        });

        mBtnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }



    //SEND IMAGE TO UPDATE
    public void uploadImageToFirebase(Uri image) {
        model.uploadImageToFirebase(image);
    }


    //SETTING VIEWS
    //mLargeUserImage
    private void setImage(User user) {
        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };

        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(user.getImageUrl()))
                .setControllerListener(listener)
                .setOldController(view.mCircleImageUser.getController())
                .build();
        view.mCircleImageUser.setController(dc);

    }

    private void setFields(User user) {
        //String date = "Perfil criado " + GetTimeAgo.getTimeAgo(user.getDate(), view.getContext());
        //view.mTvDate.setText(date);
        view.mTvProfileUserName.setText(user.getName());
        view.mTvProfileUserUsername.setText(user.getUsername());

        if (user.getAboutMe() != null || !user.getAboutMe().equals("")) {
            view.mTvAboutMe.setText(user.getAboutMe());
        }
        else if(user.getAboutMe().equals("")){
            view.mTvAboutMe.setHint(view.getResources().getString(R.string.hint_about_me));
        }

        if (mUserPresenter.getYear() != 0) {
            showAge(user);
        } else {
            view.mTvAge.setText("");
            view.mTvAge.setHint(view.getResources().getString(R.string.hint_age));
        }

        if(mUserPresenter.getGender() != null){
            view.mTvGender.setText(user.getGender());
        }
        else {
            view.mTvGender.setHint(view.getResources().getString(R.string.hint_gender));
        }

    }

    private void setToggleButton(User user) {

        view.mToggleButton.setChecked(user.isAccepted());
    }

    //TOGGLE BUTTON
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //CHECK INTERNET CONNECTION
        if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
            ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
        }
        else {
            ShowSnackBarInfoInternet.showSnack(true, view);
            //TO HANDLE CHANGED ORIENTATION
            if (mUserPresenter != null) {
                if (isChecked == mUserPresenter.isAccepted()) {
                    return;

                } else {
                    //model.updatePermissions(!mUserPresenter.isAccepted(), buttonView);
                    model.updatePermissions(isChecked, buttonView);
                    //}

                }
            }
        }

    }


    public void onDestroy() {
        subscription.clear();
        model.removeListeners();
    }*/
}