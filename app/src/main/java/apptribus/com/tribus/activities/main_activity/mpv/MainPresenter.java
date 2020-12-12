package apptribus.com.tribus.activities.main_activity.mpv;

import android.annotation.SuppressLint;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.main_activity.fragment_talks.TalksFragment;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.TimeLineFragment;
import apptribus.com.tribus.activities.main_activity.fragment_timeline.view_holder.ThematicsViewHolder;
import apptribus.com.tribus.activities.main_activity.fragment_tribus.TribusFragment;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.*;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.ADMINS;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.NEW_TRIBUS;

/**
 * Created by User on 5/25/2017.
 */

public class MainPresenter implements Toolbar.OnMenuItemClickListener{

    private final MainView view;
    private final MainModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private TimeLineFragment timeLineFragment = null;
    private TribusFragment tribusFragment = null;
    private TalksFragment talksFragment = null;
    public static boolean isOpen;
    private User mUser;
    private Boolean isFirstLoadFragmentTribusLine = true;


    //REFERENCES - FIREBASE (KEEP SYNCED)


    private FirestoreService mFirestoreService;



    public MainPresenter(MainView view, MainModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        PresenceSystemAndLastSeen.presenceSystem();

        FirebaseMessaging.getInstance().subscribeToTopic(NEW_TRIBUS);

        if (isFirstLoadFragmentTribusLine){

            if (view.timeLineFragment == null) {
                FragmentManager fm1 = view.mContext.getSupportFragmentManager();
                FragmentTransaction transaction1 = fm1.beginTransaction();

                view.timeLineFragment = TimeLineFragment.getInstance(0);
                transaction1.replace(R.id.frame_list, view.timeLineFragment).commit();
                view.tribusFragment = null;
                view.talksFragment = null;

            }

        }

        isFirstLoadFragmentTribusLine = false;

        subscription.add(observeFAB());
        subscription.add(observeModel());

        mFirestoreService = new FirestoreService(view.mContext);

        isOpen = true;

        //model.addTribuUniqueNameIntoFirestore();
        //model.addUsersNameIntoFirestore();
        //model.addTribuIntoFirestore();
        //model.getAllParticipanting();
        //model.addAdminIntoFirestore();
        //model.getAllContacts();
        //model.getAllContactsMessages();

    }

    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart() {
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {
        PresenceSystemAndLastSeen.presenceSystem();


        updateLastMessage();
    }

    public void observeUser(){
        if (mUser != null && mUser.getThumb() == null) {
            model.createThumbImage(mUser.getImageUrl(), mUser);
        }

    }


    public void openShareFragmentToCard(Tribu tribu){
        model.openShareFragmentToCard(tribu);
    }

    public void openProfileTribuUserActivity(Tribu tribu){
        model.openProfileTribuUserActivity(tribu);
    }

    public void onStop() {
        isOpen = false;
    }


    private Subscription observeModel() {
        return model.getUser()
                .subscribe(user -> {
                    mUser = user;
                            setToolbarText(user);
                            setToolbarImage(user);
                            view.mToolbarMain
                                    .setOnMenuItemClickListener(this);

                            if (view.mToolbarMain != null) {
                        setupViewsIfAdmin();
                    }
                },
                        Throwable::printStackTrace
                );
    }




    //SET SUBTITLE ACTIVITY
    private void setToolbarText(User user) {
        view.mTvNameUser.setVisibility(VISIBLE);
        view.mTvNameUser.setText(user.getName());
        view.mTvUsername.setText(user.getUsername());
    }


    //mLargeUserImage
    private void setToolbarImage(User user) {

        if (user.getThumb() != null){
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
                    .setUri(Uri.parse(user.getThumb()))
                    .setControllerListener(listener)
                    .setOldController(view.mCircleUserImage.getController())
                    .build();
            view.mCircleUserImage.setController(dc);

        }
        else {
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
                    .setOldController(view.mCircleUserImage.getController())
                    .build();
            view.mCircleUserImage.setController(dc);

        }


    }


    //Shows dialog when FAB is clicked
    private Subscription observeFAB() {
        return view.observeFAB()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .subscribe(
                        __ -> model.openCreateTribuActivity(),
                        Throwable::printStackTrace
                );
    }



    //SETUP BOTTOM NAVIGATION VIEW
    @SuppressLint("RestrictedApi")
    private void setupViewsIfAdmin() {

        view.mPrivacyPolicy.setVisible(true);
        view.mProfile.setVisible(true);

            //SET UP BOTTOM NAVIGATION VIEW - IF IS NOT ADMIN
            view.mBottomNavigationItemView.setOnNavigationItemSelectedListener(itemBottomNav -> {

                FragmentManager fm1 = model.activity.getSupportFragmentManager();
                FragmentTransaction transaction1 = fm1.beginTransaction();

                switch (itemBottomNav.getItemId()) {
                    case R.id.pesquisar_tribu:

                        view.mFab.setVisibility(VISIBLE);

                        //SET VISIBILITY OF FAB
                        //view.mAppBar.setExpanded(true);
                        //view.mToolbarMain.setCollapsible(false);
                            timeLineFragment = TimeLineFragment.getInstance(0);
                            transaction1.replace(R.id.frame_list, timeLineFragment).commit();
                            tribusFragment = null;
                            talksFragment = null;

                        return true;

                    case R.id.adicionar_tribu:

                        //SET VISIBILITY OF FAB
                        view.mFab.setVisibility(GONE);
                        //view.mAppBar.setExpanded(true);
                        //view.mToolbarMain.setCollapsible(false);

                        if (tribusFragment == null) {
                            tribusFragment = TribusFragment.getInstance(1);
                            transaction1.replace(R.id.frame_list, tribusFragment).commit();
                            timeLineFragment = null;
                            talksFragment = null;
                        }

                        return true;

                    case R.id.conversas:
                        //SET VISIBILITY OF FAB
                        view.mFab.setVisibility(GONE);

                        //view.mAppBar.setExpanded(true);
                        //view.mToolbarMain.setCollapsible(false);
                        if (talksFragment == null) {
                            talksFragment = TalksFragment.getInstance(2);
                            transaction1.replace(R.id.frame_list, talksFragment).commit();
                            timeLineFragment = null;
                            tribusFragment = null;
                        }

                        return true;
                }
                return false;

            });

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_profile_activity:
                model.openUserProfile();
                return true;

            case R.id.action_privacy_policy:
                model.openPrivacyPolicyActivity();
                return true;

            case R.id.action_share:
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                    return true;
                } else {
                    model.openShareFragmetToApp();
                    return true;
                }
        }

        return false;
    }

    private void updateLastMessage(){

        if(view.mUpdateLastMessage != null){
            model.updateLastMessage(view.mTalkerId);
        }
    }

    public void openFeatureChoiceActivity(String tribuKey, String tribuUniqueName){
        model.openFeatureChoiceActivity(tribuKey, tribuUniqueName);
    }

    public void openChatUserActivity(String contactId){
        model.openChatUserActivity(contactId);
    }

    public void openDetailContactActivity(Talk contact){
        model.openDetailContactActivity(contact.getTalkerId(), contact.getTribuKey());
    }

    public void btnFabShow(){
        view.mFab.setVisibility(VISIBLE);
    }

    public void btnFabHide(){
        view.mFab.setVisibility(GONE);
    }

    public void onDestroy() {
        subscription.clear();
    }
}
