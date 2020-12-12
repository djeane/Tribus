package apptribus.com.tribus.activities.create_tribu.mvp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.enums.EPickType;

import java.text.Normalizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;

@SuppressWarnings("Convert2MethodRef")
public class CreateTribuPresenter {

    private final CreateTribuView view;
    private final CreateTribuModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private boolean response;
    private String mTribuName;
    public static boolean isOpen;
    private ProgressDialog progress;


    public CreateTribuPresenter(CreateTribuView view, CreateTribuModel model) {
        this.view = view;
        this.model = model;
        progress = new ProgressDialog(view.mContext);

    }


    public void onStart() {


        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observerImageVerify());
        subscription.add(observeEtDescription());
        subscription.add(observerCleanText());
        subscription.add(observerEditText());
        subscription.add(observerCreateTribu());
        subscription.add(observerImage());
        subscription.add(observeEtTribuName());
        subscription.add(observeIvHelp());
        subscription.add(observeRelativeLayout());
        subscription.add(observeLabelDescription());
        subscription.add(observeTvLinkNewAdmin());
        subscription.add(observeBtnArrowBack());

        isOpen = true
                ;
    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        view.mImageDone.setEnabled(false);
        PresenceSystemAndLastSeen.presenceSystem();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            if (TextUtils.isEmpty(view.mEtTribuName.getText().toString())) {
                String username = view.mTvUniqueName.getText().toString();
                view.mTvUniqueName.setText(username);
            } else {
                String arroba = view.getContext().getResources().getString(R.string.arroba);
                String username = arroba + view.mEtTribuName.getText().toString();
                CharSequence intermediateUsername = username.replace(" ", "").toLowerCase();
                String finalUsername = Normalizer.normalize(intermediateUsername, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                view.mTvUniqueName.setText(finalUsername);
            }

        }
    }

    public void onStop(){
        isOpen = false;
    }

    //SUBSCRIBES
    //observeEtTribuName()
    private Subscription observeEtTribuName() {
        return view.observeEditText()
                .subscribe(__ -> {
                            //TO SHOW BTN CLEAR TEXT
                            if (view.mEtTribuName.getText().toString().trim().length() >= 1) {
                                view.mImageClearText.setVisibility(VISIBLE);
                                ;
                            } else {
                                view.mImageClearText.setVisibility(GONE);
                            }

                            //TO SHOW UNIQUE NAME IN TEXT VIEW
                            view.mEtTribuName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    CharSequence t = view.mContext.getResources().getString(R.string.arroba)
                                            + s.toString().replace(" ", "").toLowerCase();
                                    String username = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                                    view.mTvUniqueName.setText(username);
                                    view.mImageDone.setVisibility(GONE);
                                    view.mImageVerify.setVisibility(VISIBLE);

                                    unblockingFields(false);

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    observeEditText();
                                }
                            });
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeEtDescription() {
        return view.observeEditText()
                .subscribe(__ -> {
                            view.mEtDescription.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String appendCount = String.valueOf(s.length()) + view.mContext.getResources()
                                            .getString(R.string.character_length);

                                    view.mTvCharacterCount.setText(appendCount);

                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });
                        },
                        Throwable::printStackTrace);
    }


    private Subscription observerImageVerify() {
        return view.observeImageVerify()
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
                .map(tribuName -> getTribusName())
                .filter(tribuName -> {
                    if (TextUtils.isEmpty(tribuName)) {
                        showMessage(view.mContext.getResources().getString(R.string.toast_alert_name));
                        response = false;
                        return false;
                    }

                    //Pattern regex1 = Pattern.compile("[$#()0123456789_/.]");
                    Pattern regex1 = Pattern.compile("[$#/.]");
                    Matcher matcher1 = regex1.matcher(tribuName);
                    Pattern regex = Pattern.compile("\\[|\\]");
                    Matcher matcher = regex.matcher(tribuName);
                    Pattern regex2 = Pattern.compile("\\-|\\]");
                    Matcher matcher2 = regex2.matcher(tribuName);

                    if (matcher.find()) {
                        showMessage(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher1.find()) {
                        showMessage(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    } else if (matcher2.find()) {
                        showMessage(view.getContext().getString(R.string.invalid_special_caracter));
                        response = false;
                        return false;
                    }

                    //STORE NAME TO SAVE IT IN SHARED PREFERENCES
                    mTribuName = tribuName;
                    return true;
                })
                .doOnNext(__ -> showLoading(true, view.mContext.getResources().getString(R.string.progress_message_verifying)))
                .observeOn(Schedulers.io())
                .switchMap(tribuName -> model.verifyTribuName(getTribuUniqueName()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(tribuName -> model.saveState(tribuName))
                .doOnEach(__ -> showLoading(false, null))
                .subscribe(tribuName -> {
                            if (tribuName == null) {
                                showMessage(view.mContext.getResources().getString(R.string.name_available));
                                view.mImageVerify.setVisibility(INVISIBLE);
                                view.mImageDone.setVisibility(VISIBLE);
                                view.mEtTribuName.clearFocus();
                                this.unblockingFields(true);
                            } else if (tribuName.equals("@") || tribuName.equals("")) {
                                showLoading(false, null);
                                showMessage(view.mContext.getResources().getString(R.string.toast_alert_tribu_name_empty));
                            } else {
                                showMessage(view.mContext.getResources().getString(R.string.name_unavailable));
                            }
                        },
                        Throwable::printStackTrace);
    }


    private Subscription observerCleanText() {
        return view.observeImageClearText()
                .subscribe(__ -> {
                            view.mEtTribuName.setText("");
                            view.mImageDone.setVisibility(GONE);
                            view.mImageVerify.setVisibility(VISIBLE);
                            this.unblockingFields(false);
                        },
                        Throwable::printStackTrace);
    }

    private String getTribuUniqueName(){
        return view.mTvUniqueName.getText().toString().trim();
    }

    private Subscription observerEditText() {
        return view.observeEtTribuName()
                .subscribe(__ -> {

                            if (view.mImageVerify.getVisibility() == VISIBLE) {
                                unblockingFields(false);
                            } else {
                                view.mImageDone.setVisibility(VISIBLE);
                                view.mImageVerify.setVisibility(GONE);

                                unblockingFields(true);

                            }
             },
                        Throwable::printStackTrace);
    }

    private Subscription observeRelativeLayout(){
        return view.observeRelativeLayout()
                .subscribe(__ ->{
                    view.mRelativeLayout.requestFocus();
                    if(view.mImageDone.getVisibility() != VISIBLE){
                        showMessage(view.mContext.getString(R.string.info_verify_first));
                    }
                    else {
                        view.mEtDescription.setHintTextColor(view.mContext.getResources().getColor(R.color.colorPrimaryDark));
                        view.mLabelDescription.setHintTextAppearance(R.style.style_hint);
                        View viewFocus = view.mContext.getCurrentFocus();
                        if (viewFocus != null) {
                            InputMethodManager imm = (InputMethodManager) view.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                        }
                    }
                });
    }

    private Subscription observeLabelDescription(){
        return view.observeLabelDescription()
                .subscribe(__ ->{
                    if(view.mImageDone.getVisibility() != VISIBLE){
                        showMessage(view.mContext.getString(R.string.info_verify_first));
                    }
                });
    }


    private Subscription observeTvLinkNewAdmin(){
        return view.observeTvLinkNewAdmin()
                .subscribe(__ -> {
                    openDialogInfoNewAdmin();
                });
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
        builder.setTitle(view.mContext.getResources().getString(R.string.about_unique_name));
        builder.setMessage(view.getContext().getString(R.string.info_unique_name_tribus));


        String positiveText = view.mContext.getResources().getString(R.string.ok);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void openDialogInfoNewAdmin() {

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext);
        builder.setMessage(view.getContext().getString(R.string.info_tribus));

        String positiveText = view.mContext.getResources().getString(R.string.ok);
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();

        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }


    //observeCreateTribu()
    private Subscription observerCreateTribu() {
        return view.observeCreateTribu()
                .doOnNext(__ -> view.mBtnCreate.setEnabled(false))
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        view.mBtnCreate.setEnabled(true);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .filter(__ -> {
                    if (!this.validateFields()) {
                        view.mBtnCreate.setEnabled(true);
                        response = false;
                    } else {

                        response = true;
                    }
                    return response;
                })
                .doOnEach(__ -> showLoading(true, view.mContext.getResources().getString(R.string.progress_message_creating_tribu)))
                .observeOn(Schedulers.io())
                .switchMap(uid -> model.getCurrentUser())
                .map(user -> view.getTribu(user))
                .doOnNext(tribu -> model.createTribu(tribu))
                .doOnNext(tribu -> model.saveUniqueName(tribu))
                .doOnNext(tribu -> model.saveAdmin(tribu))
                .doOnNext(tribu -> model.saveParticipantAdmin(tribu))
                .doOnNext(tribu -> model.updateThematics(tribu))
                .doOnNext(tribu -> model.storageImageTribu(tribu))


                /*.filter(__ -> {
                    if (!model.createTribu(__)) {
                        view.observeCreateTribu().retry();
                        view.showLoading(false, null);
                        view.showMessage("Houve um erro ao criar a mTribu");
                        response = false;
                    } else {
                        response = true;
                    }
                    return response;
                })
                .filter(__ -> true)*/
                //.debounce(10000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(__ -> showLoading(false, null))
                .retry()
                .subscribe(response -> {
                            showMessage(view.mContext.getResources().getString(R.string.tribu_created_successfully));
                            model.startMainActivity();
                        },
                        e -> {
                            e.printStackTrace();
                            Toast.makeText(view.mContext,
                                    view.mContext.getResources().getString(R.string.toast_error_create_tribu),
                                    Toast.LENGTH_SHORT).show();

                        });

    }


    //GET NAME
    @NonNull
    private String getTribusName() {
        return view.mEtTribuName.getText().toString();
    }


    public void getImage() {
        model.getImage();
    }



    private Subscription observerImage() {
        return view.observeImage()
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                .subscribe(__ -> {
                            showDialogToChooseImage();
                        },
                        Throwable::printStackTrace);
    }


    //SAVING STATE
    private Subscription loadSavedState() {
        return model.getUserFromSaveState()
                .subscribe(tribuName -> {
                            if (tribuName == null) {
                                view.mImageVerify.setVisibility(GONE);
                                view.mImageDone.setVisibility(VISIBLE);
                                view.requestFocus();
                                this.unblockingFields(true);
                            }

                        },
                        Throwable::printStackTrace);
    }

    private void showDialogToChooseImage() {

        PickSetup setup = new PickSetup()
                .setTitle(view.mContext.getResources().getString(R.string.title_choose))
                .setTitleColor(view.getResources().getColor(R.color.primary_text))
                .setBackgroundColor(Color.WHITE)
                .setCancelText(view.mContext.getResources().getString(R.string.title_cancel))
                .setFlip(true)
                .setMaxSize(500)
                .setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
                .setCameraButtonText(view.mContext.getResources().getString(R.string.title_camera))
                .setGalleryButtonText(view.mContext.getResources().getString(R.string.title_galery))
                .setIconGravity(Gravity.LEFT)
                .setButtonOrientation(LinearLayoutCompat.VERTICAL)
                .setSystemDialog(false)
                .setCameraIcon(R.mipmap.camera_colored)
                .setGalleryIcon(R.mipmap.gallery_colored);

        PickImageDialog.build(setup).show(view.mContext);
    }



    private void unblockingFields(boolean block){
        view.mSdTribuImage.setEnabled(block);
        view.mEtDescription.setEnabled(block);
        view.mSdTribuImage.setEnabled(block);
        view.mSpThematic.setEnabled(block);
        view.mBtnCreate.setEnabled(block);
        view.mToggleButton.setEnabled(block);
        view.mTvLinkNewAdmin.setEnabled(block);

        if (!block) {

            view.mBtnCreate.setBackgroundColor(view.getResources().getColor(R.color.hintBlockedFields));
            view.mEtTribuName.setHintTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mEtDescription.setHintTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mEtDescription.setTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mTvQuestion.setTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mTvCharacterCount.setTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mToggleButton.setTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));
            view.mTvLinkNewAdmin.setTextColor(view.mContext.getResources().getColor(R.color.hintBlockedFields));


        } else {
            view.mBtnCreate.setBackgroundResource(R.drawable.button_check_username);
            view.mEtTribuName.setHintTextColor(view.mContext.getResources().getColor(R.color.colorPrimaryDark));
            view.mEtDescription.setTextColor(view.mContext.getResources().getColor(R.color.primary_text));
            view.mTvQuestion.setTextColor(view.mContext.getResources().getColor(R.color.colorPrimaryDark));
            view.mTvCharacterCount.setTextColor(view.mContext.getResources().getColor(R.color.primary_text));
            view.mToggleButton.setTextColor(view.mContext.getResources().getColor(R.color.primary_text));
            view.mTvLinkNewAdmin.setTextColor(view.mContext.getResources().getColor(R.color.colorAccent));

        }
    }


    private void observeEditText() {
        if (TextUtils.isEmpty(view.mEtTribuName.getText().toString())) {
            view.mTvUniqueName.setText(view.getContext().getResources().getString(R.string.tv_unique_name_tribu));
        }
    }


    private boolean validateFields() {
        boolean isEmpty = true;
        if (view.mEtTribuName.getText().toString().trim().equals("")) {
            showMessage(view.mContext.getResources().getString(R.string.toast_error_tribu_name_empty));
            view.mBtnCreate.setEnabled(true);
            isEmpty = false;
        } else if (view.mEtDescription.getText().toString().trim().equals("")) {
            showMessage(view.mContext.getResources().getString(R.string.toast_error_tribu_description_empty));
            view.mBtnCreate.setEnabled(true);
            isEmpty = false;
        } else if (view.mSpThematic.getSelectedItem().toString().trim().equals(view.mContext.getResources().getString(R.string.chose_thematic))) {
            showMessage(view.mContext.getResources().getString(R.string.toast_error_tribu_thematic_empty));
            view.mBtnCreate.setEnabled(true);
            isEmpty = false;
        }
        return isEmpty;
    }


    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(
                        __ -> model.backToMainActivity(),
                        Throwable::printStackTrace);
    }

    private void showMessage(String message) {
        Toast.makeText(view.mContext, message, Toast.LENGTH_SHORT).show();
    }


    private void showLoading(boolean loading, String message) {

        progress.setMessage(message);
        progress.setCancelable(false);

        if (loading) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }



    public void onDestroy() {
        subscription.clear();
    }


}
