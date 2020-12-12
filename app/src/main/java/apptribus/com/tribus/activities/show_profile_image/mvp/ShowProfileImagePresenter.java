package apptribus.com.tribus.activities.show_profile_image.mvp;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;

/**
 * Created by User on 12/25/2017.
 */

public class ShowProfileImagePresenter implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener, Toolbar.OnMenuItemClickListener{

    private final ShowProfileImageView view;
    private final ShowProfileImageModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);

    //TO SHOW IMPLEMENT LEAN BACK
    private final Handler mLeanBackHandler = new Handler();
    private int mLastSystemUIVisibility;
    private final Runnable mEnterLeanback = () -> enableFullScreen(true);

    public ShowProfileImagePresenter(ShowProfileImageView view, ShowProfileImageModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){

        //KEEP SYNCED
        mReferenceUser.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();
        subscription.add(observeBtnArrowBack());

        view.mMainView = view.findViewById(getMainViewID());
        view.mMainView.setClickable(true);

        //TO IMPLEMENT LEAN BACK
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

    //RELEASE PLAYER AND FINISH ACTIVITY - WORKING
    private Subscription observeBtnArrowBack(){
        return view.observableBtnArrowBack()
                .subscribe(
                        __ -> view.mContext.finish(),
                        Throwable::printStackTrace
                );
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
                .setOldController(view.mZoomDraweeViewShowImage.getController())
                .build();
        view.mZoomDraweeViewShowImage.setController(dc);

    }


    //TO SHOW LEAN BACK
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)view.mRelativeRoot.getLayoutParams();

            setViewMargins(view.mContext, params, 0, 0, 35, 0, view.mRelativeRoot);

            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);

        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)view.mRelativeRoot.getLayoutParams();

            setViewMargins(view.mContext, params2, 0, 0, 0, 0, view.mRelativeRoot);
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

        // First cancel any queued events - i.e. resetting the countdown clock
        mLeanBackHandler.removeCallbacks(mEnterLeanback);
        // And fire the event in 3s time
        mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
    }


    public void onDestroy(){
        subscription.clear();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            /*case R.id.action_download:
                model.downloadImage(view.mImageUri);
                break;*/

            case R.id.action_share:
                model.openShareImage(view.mImageUri);
                break;

        }

        return true;

    }
}
