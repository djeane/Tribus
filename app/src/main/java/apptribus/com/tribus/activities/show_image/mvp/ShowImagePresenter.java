package apptribus.com.tribus.activities.show_image.mvp;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import butterknife.OnTouch;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 7/20/2017.
 */

public class ShowImagePresenter implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener{

    private final ShowImageView view;
    private final ShowImageModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;



    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);


    //TO SHOW IMPLEMENT LEAN BACK
    private final Handler mLeanBackHandler = new Handler();
    private int mLastSystemUIVisibility;
    private final Runnable mEnterLeanback = () -> enableFullScreen(true);


    public ShowImagePresenter(ShowImageView view, ShowImageModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mRefTribusMessage.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeTribu());
        subscription.add(observeMessage());
        subscription.add(observeBtnArrowBack());

        //TO IMPLEMENT LEAN BACK
        view.mMainView = view.findViewById(getMainViewID());
        view.mMainView.setClickable(true);

        getDecorView().setOnSystemUiVisibilityChangeListener(this);
        getMainView().setOnClickListener(this);

        setSimpleDraweeView();


        resetHideTimer();

        isOpen = true;
    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onStop(){
        isOpen = false;
    }

    //SUBSCRIPTIONS
    private Subscription observeTribu(){
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                    setFields(tribu);
                    setImage(tribu);
                },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeMessage(){
        return model.getMessage(view.mTribuKey, view.mMessageReference)
                .subscribe(messageUser -> {
                    if (messageUser.getImage().getDescription() != null){
                        view.mTvDescription.setVisibility(VISIBLE);
                        view.mTvDescription.setText(messageUser.getImage().getDescription().trim());
                    }
                    else {
                        view.mTvDescription.setVisibility(GONE);
                    }
                },
                        Throwable::printStackTrace
                );
    }

    //RELEASE PLAYER AND FINISH ACTIVITY - WORKING
    private Subscription observeBtnArrowBack(){
        return view.observableBtnArrowBack()
                .subscribe(__ -> {
                    model.backToChatTribuActivity();
                },
                        Throwable::printStackTrace
                );
    }


    //SET NAME'S TRIBU
    private void setFields(Tribu tribu){
        view.mTribuName.setText(tribu.getProfile().getNameTribu());
        view.mTribuUniqueName.setText(tribu.getProfile().getUniqueName());
    }

    //SETTING IMAGE
    private void setImage(Tribu tribu){
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
                .setUri(Uri.parse(tribu.getProfile().getImageUrl()))
                .setControllerListener(listener)
                .setOldController(view.mCircleTribuImage.getController())
                .build();
        view.mCircleTribuImage.setController(dc);

    }

    private void setSimpleDraweeView(){
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
                .setUri(view.mImageUri)
                .setControllerListener(listener)
                .setOldController(view.mSimpleDraweeViewShowImage.getController())
                .build();
        view.mSimpleDraweeViewShowImage.setController(dc);

    }


    //TO SHOW LEAN BACK
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.mRelativeRoot.getLayoutParams();

            setViewMargins(view.mContext, params, 0, 0, 35, 0, view.mRelativeRoot);

            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);


            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)view.mCardShowDescription.getLayoutParams();

            setViewMargins(view.mContext, params2, 0, 0, 0, 0, view.mCardShowDescription);

            //setViewMargins(view.mContext, params, 35, 0, 0, 0, view.mCardShowDescription);

            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.mCardShowDescription.getLayoutParams();
            //params.setMargins(0, 0, 0, 0); //substitute parameters for left, top, right, bottom
            //view.mCardShowDescription.setLayoutParams(params);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.mCardShowDescription.getLayoutParams();

            setViewMargins(view.mContext, params, 0, 0, 0, 50, view.mCardShowDescription);

            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)view.mRelativeRoot.getLayoutParams();

            setViewMargins(view.mContext, params2, 0, 0, 0, 0, view.mRelativeRoot);


            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.mCardShowDescription.getLayoutParams();
            //params.setMargins(0, 0, 0, 60); //substitute parameters for left, top, right, bottom
            //view.mCardShowDescription.setLayoutParams(params);
        }
    }

    private void setViewMargins(Context con, ViewGroup.LayoutParams params,
                                int left, int top , int right, int bottom, View view) {

        final float scale = con.getResources().getDisplayMetrics().density;
        // convert the DP into pixel
        int pixel_left = (int) (left * scale + 0.5f);
        int pixel_top = (int) (top * scale + 0.5f);
        int pixel_right = (int) (right * scale + 0.5f);
        int pixel_bottom = (int) (bottom * scale + 0.5f);

        ViewGroup.MarginLayoutParams s = (ViewGroup.MarginLayoutParams) params;
        s.setMargins(pixel_left, pixel_top, pixel_right, pixel_bottom);

        view.setLayoutParams(params);
    }


    private int getMainViewID() {
        return R.id.root;
    }

    protected final View getDecorView() {
        return view.mDecorView;
    }

    protected final View getMainView() {
        return view.mMainView;
    }


    @Override
    public void onClick(View v) {
        // If the `mainView` receives a click event then reset the leanback-mode clock
        resetHideTimer();

    }


    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if((mLastSystemUIVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) != 0
                && (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {

            //view.mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //view.mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



            resetHideTimer();
        }
        mLastSystemUIVisibility = visibility;
    }


    private void enableFullScreen(boolean enabled) {
        int newVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        if(enabled) {
            newVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN
                    |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

            view.mToolbarShowImage.setVisibility(GONE);
            view.mAppBar.setVisibility(GONE);
            view.mCardShowDescription.setVisibility(GONE);

            //view.mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //view.mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);


        }

        // Want to hide again after 3s
        if(!enabled) {
            resetHideTimer();
        }

        // Set the visibility
        getDecorView().setSystemUiVisibility(newVisibility);
    }

    private void resetHideTimer() {
        view.mAppBar.setVisibility(VISIBLE);
        view.mToolbarShowImage.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(view.mTvDescription.getText().toString().trim())) {
            view.mCardShowDescription.setVisibility(VISIBLE);
            view.mTvDescription.setVisibility(VISIBLE);
        } else {
            view.mCardShowDescription.setVisibility(GONE);
            view.mTvDescription.setVisibility(GONE);
        }

        //view.mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // First cancel any queued events - i.e. resetting the countdown clock
        mLeanBackHandler.removeCallbacks(mEnterLeanback);
        // And fire the event in 3s time
        mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
    }

    public void onDestroy(){
        subscription.clear();
    }

}
