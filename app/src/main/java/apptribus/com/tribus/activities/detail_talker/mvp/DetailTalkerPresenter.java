package apptribus.com.tribus.activities.detail_talker.mvp;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class DetailTalkerPresenter {

    private final DetailTalkerView view;
    private final DetailTalkerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;
    private User mUserNoMetterIsContact;
    private User mContact;
    private User mContactWaitingPermission;
    private User mContactInvited;
    private User mUserNoContact;
    private List<Tribu> mTribusList;


    public DetailTalkerPresenter(DetailTalkerView view, DetailTalkerModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        subscription.add(getUserNoMetterIsContact());
        subscription.add(getAdminsNumber());
        subscription.add(observeContact());
        //subscription.add(getUserThatIsNotAContact());
        //subscription.add(getContactWaitingPermission());
        //subscription.add(getContactInvited());

        subscription.add(observeProfileImage());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeNumTribus());
        subscription.add(observeNumContacts());
        subscription.add(observeBtnUpdate());
        subscription.add(observeBtnProfile());
        subscription.add(observeBtnRemoveTalker());
        subscription.add(observeBtnAddContact());
        subscription.add(observeBtnContactPrivateAccept());
        subscription.add(observableBtnDeniedPrivate());
        subscription.add(observableBtnAcceptTalkerPublic());
        subscription.add(observableBtnDeniedPublic());
        subscription.add(observableBtnPrivateAdded());
        subscription.add(observableBtnCancelPrivateAdded());

        PresenceSystemAndLastSeen.presenceSystem();


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
    }

    public void onStop() {
        isOpen = false;
    }


    private Subscription getUserNoMetterIsContact(){
        return model.getUserNoMetterIsContact(view.mContactId)
                .subscribe(userNoMetterIsContact -> {

                    setImageTalker(userNoMetterIsContact);
                    setTvName(userNoMetterIsContact);
                    setTvUsername(userNoMetterIsContact);

                });

    }

    private Subscription getAdminsNumber(){
        return model.getAdminsNumber(new ArrayList<>(), view.mContactId)
                .subscribe(this::serAdminsNumber);
    }

    private Subscription observeContact(){
        return model.getContact(view.mContactId, view.mUserId)
                .concatMap(contact -> {
                    mContact = contact;
                    return model.getUserWaitingPermission(view.mContactId, view.mUserId);
                })
                .concatMap(contactWaitingPermission -> {
                    mContactWaitingPermission = contactWaitingPermission;
                    return model.getUserInvited(view.mContactId, view.mUserId);
                })
                .concatMap(userIvited -> {
                    mContactInvited = userIvited;
                    return model.getUserThatIsNotAContact(view.mUserId, view.mContactId);
                })
                .subscribe(userIsNotContact -> {
                    mUserNoContact = userIsNotContact;

                    if (mContact != null) {
                        setGender(mContact);
                        setTvStatus(mContact);
                        showAge(mContact);

                        view.mRelativeMiddle.setVisibility(VISIBLE);
                        view.mRelativeBottom.setVisibility(VISIBLE);
                        view.mRelativePrivate.setVisibility(GONE);
                        view.mLinearButtonsPublicAdd.setVisibility(VISIBLE);
                        view.mLinearButtonsPublicAccept.setVisibility(GONE);
                        view.mBtnRemoveTalker.setVisibility(VISIBLE);
                        view.mBtnAddTalker.setVisibility(GONE);

                        view.mRelativeProgress.setVisibility(GONE);
                    }
                    else if (mContactWaitingPermission != null){

                        //setImageTalker(mContactWaitingPermission);
                        //setTvName(mContactWaitingPermission);
                        //setTvUsername(mContactWaitingPermission);

                        if (mContactWaitingPermission.isAccepted()) {
                            //foi convidado
                            view.mRelativeMiddle.setVisibility(VISIBLE);
                            view.mRelativeBottom.setVisibility(VISIBLE);
                            view.mRelativePrivate.setVisibility(GONE);
                            setGender(mContactWaitingPermission);
                            setTvStatus(mContactWaitingPermission);
                            showAge(mContactWaitingPermission);
                            view.mLinearButtonsPublicAccept.setVisibility(VISIBLE);
                            view.mLinearButtonsPublicAdd.setVisibility(GONE);

                        }
                        else {
                            view.mRelativeMiddle.setVisibility(GONE);
                            view.mRelativeBottom.setVisibility(GONE);
                            view.mRelativePrivate.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAccept.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAdded.setVisibility(GONE);
                        }

                        view.mRelativeProgress.setVisibility(GONE);
                    }
                    else if (mContactInvited != null){
                        //setImageTalker(mContactInvited);
                        //setTvName(mContactInvited);
                        //setTvUsername(mContactInvited);

                        view.mRelativeMiddle.setVisibility(GONE);
                        view.mRelativeBottom.setVisibility(GONE);
                        view.mRelativePrivate.setVisibility(VISIBLE);
                        view.mBtnPrivateAdded.setVisibility(GONE);
                        view.mLinearButtonsPrivateAdded.setVisibility(VISIBLE);
                        view.mBtnCancelPrivateAdded.setVisibility(VISIBLE);
                        view.mLinearButtonsPrivateAccept.setVisibility(GONE);

                        view.mRelativeProgress.setVisibility(GONE);
                    }
                    else if (mUserNoContact != null){
                        //setImageTalker(mUserNoContact);
                        //setTvName(mUserNoContact);
                        //setTvUsername(mUserNoContact);

                        if (mUserNoContact.isAccepted()) {
                            view.mRelativeMiddle.setVisibility(VISIBLE);
                            view.mRelativeBottom.setVisibility(VISIBLE);
                            view.mRelativePrivate.setVisibility(GONE);
                            setGender(mUserNoContact);
                            setTvStatus(mUserNoContact);
                            showAge(mUserNoContact);
                            view.mLinearButtonsPublicAdd.setVisibility(VISIBLE);
                            view.mLinearButtonsPublicAccept.setVisibility(GONE);
                            view.mBtnAddTalker.setVisibility(VISIBLE);
                            view.mBtnRemoveTalker.setVisibility(GONE);
                        }
                        else {

                            view.mRelativeMiddle.setVisibility(GONE);
                            view.mRelativeBottom.setVisibility(GONE);
                            view.mRelativePrivate.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAdded.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAccept.setVisibility(GONE);
                            view.mBtnPrivateAdded.setVisibility(VISIBLE);
                            view.mBtnCancelPrivateAdded.setVisibility(GONE);

                        }

                        view.mRelativeProgress.setVisibility(GONE);
                    }

                });

    }

    //verificar se é contato
    private Subscription observeContactNew() {
        return model.getContact(view.mContactId, view.mUserId)
                .concatMap(talker -> {
                    mContact = talker;
                    return model.getAdminsNumber(new ArrayList<>(), talker.getId());
                })
                .subscribe(tribus -> {
                            serAdminsNumber(tribus);
                            if (mContact != null) {
                                setImageTalker(mContact);
                                setTvName(mContact);
                                setTvUsername(mContact);
                                view.mRelativeMiddle.setVisibility(VISIBLE);
                                view.mRelativeBottom.setVisibility(VISIBLE);
                                view.mRelativePrivate.setVisibility(GONE);
                                view.mLinearButtonsPublicAdd.setVisibility(VISIBLE);
                                view.mLinearButtonsPublicAccept.setVisibility(GONE);
                                view.mBtnRemoveTalker.setVisibility(VISIBLE);
                                view.mBtnAddTalker.setVisibility(GONE);
                                setGender(mContact);
                                setTvStatus(mContact);
                                showAge(mContact);

                            }

                        },
                        Throwable::printStackTrace
                );
    }

    //se não for contato, verificar se está aguardando ser adicionado
    private Subscription getContactWaitingPermission(){
        return model.getUserWaitingPermission(view.mContactId, view.mUserId)
                .subscribe(userWaitingPermission -> {

                    if (userWaitingPermission != null){

                        setImageTalker(userWaitingPermission);
                        setTvName(userWaitingPermission);
                        setTvUsername(userWaitingPermission);

                        if (userWaitingPermission.isAccepted()) {
                            //foi convidado
                            view.mRelativeMiddle.setVisibility(VISIBLE);
                            view.mRelativeBottom.setVisibility(VISIBLE);
                            view.mRelativePrivate.setVisibility(GONE);
                            setGender(userWaitingPermission);
                            setTvStatus(userWaitingPermission);
                            showAge(userWaitingPermission);
                            view.mLinearButtonsPublicAccept.setVisibility(VISIBLE);
                            view.mLinearButtonsPublicAdd.setVisibility(GONE);

                        } else {
                            view.mRelativeMiddle.setVisibility(GONE);
                            view.mRelativeBottom.setVisibility(GONE);

                            view.mRelativePrivate.setVisibility(VISIBLE);

                            view.mLinearButtonsPrivateAccept.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAdded.setVisibility(GONE);
                        }
                    }

                });
    }

    //se não for contato, verificar se foi convidado
    private Subscription getContactInvited(){
        return model.getUserInvited(view.mContactId, view.mUserId)
                .subscribe(contactInvited -> {

                    if (contactInvited != null){

                        setImageTalker(contactInvited);
                        setTvName(contactInvited);
                        setTvUsername(contactInvited);

                        /*if (contactInvited.isAccepted()) {
                            //foi convidado
                            view.mRelativeMiddle.setVisibility(View.VISIBLE);
                            view.mRelativeBottom.setVisibility(View.VISIBLE);
                            view.mRelativePrivate.setVisibility(View.GONE);
                            setGender(contactInvited);
                            setTvStatus(contactInvited);
                            showAge(contactInvited);
                            view.mLinearButtonsPublicAccept.setVisibility(View.VISIBLE);
                            view.mLinearButtonsPublicAdd.setVisibility(View.GONE);

                        } else {*/
                            view.mRelativeMiddle.setVisibility(GONE);
                            view.mRelativeBottom.setVisibility(GONE);

                            view.mRelativePrivate.setVisibility(VISIBLE);

                            view.mBtnPrivateAdded.setVisibility(GONE);
                            view.mBtnCancelPrivateAdded.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAdded.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAccept.setVisibility(GONE);
                        //}
                    }

                });
    }

    private Subscription getUserThatIsNotAContact(){
        return model.getUserThatIsNotAContact(view.mUserId, view.mContactId)
                .subscribe(user -> {
                    if (user != null) {

                        setImageTalker(user);
                        setTvName(user);
                        setTvUsername(user);

                        if (user.isAccepted()) {
                            view.mRelativeMiddle.setVisibility(VISIBLE);
                            view.mRelativeBottom.setVisibility(VISIBLE);
                            view.mRelativePrivate.setVisibility(GONE);
                            setGender(user);
                            setTvStatus(user);
                            showAge(user);

                            view.mLinearButtonsPublicAdd.setVisibility(VISIBLE);
                            view.mLinearButtonsPublicAccept.setVisibility(GONE);

                            view.mBtnAddTalker.setVisibility(VISIBLE);
                            view.mBtnRemoveTalker.setVisibility(GONE);
                        }
                        else {

                            view.mRelativeMiddle.setVisibility(GONE);
                            view.mRelativeBottom.setVisibility(GONE);
                            view.mRelativePrivate.setVisibility(VISIBLE);

                            view.mLinearButtonsPrivateAdded.setVisibility(VISIBLE);
                            view.mLinearButtonsPrivateAccept.setVisibility(GONE);

                            view.mBtnPrivateAdded.setVisibility(VISIBLE);
                            view.mBtnCancelPrivateAdded.setVisibility(GONE);

                        }
                    }
                });
    }



    private void serAdminsNumber(List<Tribu> tribus){
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

    }



    private Subscription observeNumContacts() {
        return model.getNumContacts(view.mContactId)
                .subscribe(this::setNumContacts,
                        Throwable::printStackTrace
                );
    }

    private Subscription observeNumTribus() {
        return model.getNumTribus(view.mContactId)
                .subscribe(this::setNumTribus,
                        Throwable::printStackTrace
                );
    }

    private Subscription observeProfileImage() {
        return view.observeProfileImage()
                .subscribe(__ -> {
                            if (mContact != null) {
                                model.openShowProfileImageActivity(mContact.getImageUrl());
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBabk()
                .subscribe(__ -> {
                            if (view.fromNotification != null) {
                                Intent intent = new Intent(view.mContext, MainActivity.class);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            } else {
                                model.backToMainActivity();
                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnUpdate() {
        return view.observeBtnUpdate()
                .subscribe(__ -> {
                            view.mBtnUpdate.setBackground(view.getResources().getDrawable(R.drawable.button_options_talker_update_pressed));
                            view.mBtnUpdate.setTextColor(view.getResources().getColor(R.color.accent));

                            view.mBtnProfile.setBackground(view.getResources().getDrawable(R.drawable.button_options_talker_profile));
                            view.mBtnProfile.setTextColor(view.getResources().getColor(R.color.primary_text));

                            Toast.makeText(view.mContext, "Aguarde! Logo essa novidade estará disponível!", Toast.LENGTH_SHORT)
                                    .show();
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnProfile() {
        return view.observeBtnProfile()
                .subscribe(__ -> {
                            view.mBtnProfile.setBackground(view.getResources().getDrawable(R.drawable.button_options_talker_profile_pressed));
                            view.mBtnProfile.setTextColor(view.getResources().getColor(R.color.accent));

                            view.mBtnUpdate.setBackground(view.getResources().getDrawable(R.drawable.button_options_talker_update));
                            view.mBtnUpdate.setTextColor(view.getResources().getColor(R.color.primary_text));

                            view.mRelativePrivate.setVisibility(GONE);
                            view.mRelativeProfile.setVisibility(VISIBLE);
                            view.mRvUpdate.setVisibility(GONE);
                        },
                        Throwable::printStackTrace
                );
    }

    //BUTTONS SUBSCRPTIONS
    private Subscription observeBtnRemoveTalker() {
        return view.observableBtnRemoveTalker()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.removeContact(view.mContactId, view.mFromChatTribus);

                    }
                })
                .subscribe();
    }


    private Subscription observeBtnAddContact(){
        return view.observableBtnAddContact()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.addContact(view.mTribusKey, view.mContactId, view);

                    }
                })
                .subscribe();
    }



    private Subscription observeBtnContactPrivateAccept(){
        return view.observableBtnTalkerPrivateAccept()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.addContactIfInvitedAndProfilePrivate(view.mTribusKey, view.mContactId, view);

                    }
                })
                .subscribe();
    }

    private Subscription observableBtnDeniedPrivate(){
        return view.observableBtnDeniedPrivate()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.excludeInvitationIfProfilePrivateAndPublic(view.mContactId, view);

                    }
                })
                .subscribe();
    }

    private Subscription observableBtnAcceptTalkerPublic(){
        return view.observableBtnAcceptTalkerPublic()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.addContactIfInvitedAndProfilePrivate(view.mTribusKey, view.mContactId, view);

                    }
                })
                .subscribe();
    }

    private Subscription observableBtnDeniedPublic(){
        return view.observableBtnDeniedPublic()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.excludeInvitationIfProfilePrivateAndPublic(view.mContactId, view);
                    }
                })
                .subscribe();
    }

    private Subscription observableBtnPrivateAdded(){
        return view.observableBtnPrivateAdded()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.showDialogAddTalkerIfPrivate(view.mTribusKey, view.mContactId, view);

                    }
                })
                .subscribe();
    }

    private Subscription observableBtnCancelPrivateAdded(){
        return view.observableBtnCancelPrivateAdded()
                .doOnNext(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    } else {
                        model.cancelInvitationTalkerPrivate(view.mContactId, view);
                    }
                })
                .subscribe();
    }

    private void setNumContacts(Integer numContacts) {
        String appendNumContacts;

        if (numContacts == 0 || numContacts == 1) {
            appendNumContacts = String.valueOf(numContacts) + " " + view.mContext.getResources().getString(R.string.one_contact);
        } else {
            appendNumContacts = String.valueOf(numContacts) + " " + view.mContext.getResources().getString(R.string.more_than_one_contact);
        }

        view.mTvNumContacts.setText(appendNumContacts);
    }

    private void setNumTribus(Integer numTribus) {
        String appendNumTribus;

        if (numTribus == 0 || numTribus == 1) {
            appendNumTribus = view.mContext.getResources().getString(R.string.participating) + " "
                    + String.valueOf(numTribus) + " " + view.mContext.getResources().getString(R.string.one_tribu);
        } else {
            appendNumTribus = view.mContext.getResources().getString(R.string.participating) + " "
                    + String.valueOf(numTribus) + " " + view.mContext.getResources().getString(R.string.more_than_one_tribu);
        }

        view.mTvNumTribus.setText(appendNumTribus);
    }

    private void setTvName(User talker) {
        view.mTvName.setText(talker.getName());
    }

    private void setTvUsername(User talker) {
        view.mTvUsername.setText(talker.getUsername());
    }

    private void setTvStatus(User user){
        view.mTvStatus.setText(user.getAboutMe());
    }

    private void setImageTalker(User talker) {
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

        String imageTalker;
        if (talker.getThumb() != null) {
            imageTalker = talker.getThumb();
        } else {
            imageTalker = talker.getImageUrl();
        }

        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(imageTalker))
                .setControllerListener(listener)
                .setOldController(view.mSdCircleImageTalker.getController())
                .build();
        view.mSdCircleImageTalker.setController(dc);

    }

    private void showAge(User user) {

        if (user.getYear() != 0) {

            int age;
            final Calendar calenderToday = Calendar.getInstance();
            int currentYear = calenderToday.get(Calendar.YEAR);
            int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
            int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

            age = currentYear - user.getYear();

            if (user.getMonth() > currentMonth) {
                --age;
            } else if (user.getMonth() == currentMonth) {
                if (user.getDay() > todayDay) {
                    --age;
                }
            }

            String appendAge = view.getResources().getString(R.string.i_have)
                    + " " + age + " " + view.getResources().getString(R.string.years);

            view.mTvAge.setHint("");
            view.mTvAge.setText(appendAge);
        } else {
            view.mTvAge.setText("");
            String appendAgeHint = user.getName() + " " + view.getResources().getString(R.string.age_yet);
            view.mTvAge.setHint(appendAgeHint);
        }
    }

    private void setGender(User talker) {
        view.mTvGender.setText(talker.getGender());
    }

    public void onDestroy() {

        subscription.clear();

    }
}
