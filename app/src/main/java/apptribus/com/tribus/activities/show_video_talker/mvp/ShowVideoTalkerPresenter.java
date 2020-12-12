package apptribus.com.tribus.activities.show_video_talker.mvp;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Formatter;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;

/**
 * Created by User on 8/16/2017.
 */

public class ShowVideoTalkerPresenter implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener{

    private final ShowVideoTalkerView view;
    private final ShowVideoTalkerModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    public static boolean isOpen;


    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mRefUsersTalkMessage = mDatabase.getReference().child(TALKERS_MESSAGES);

    //TO SHOW IMPLEMENT LEAN BACK
    private final Handler mLeanBackHandler = new Handler();
    private int mLastSystemUIVisibility;
    private final Runnable mEnterLeanback = () -> enableFullScreen(true);


    public ShowVideoTalkerPresenter(ShowVideoTalkerView view, ShowVideoTalkerModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){
        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mRefUsersTalkMessage.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        subscription.add(observeTalker());
        subscription.add(observeMessage());
        subscription.add(observeBtnArrowBack());

        if(view.mHandler == null){
            view.mHandler = new Handler();
        }
        if(view.mHandlerTouch == null) {
            view.mHandlerTouch = new Handler();
        }

        if(view.mHandlerFinished == null){
            view.mHandlerFinished = new Handler();
        }
        view.mIsNotFirstPlay = false;

        view.mMainView = view.findViewById(getMainViewID());
        view.mMainView.setClickable(true);

        //TO IMPLEMENT LEAN BACK
        getDecorView().setOnSystemUiVisibilityChangeListener(this);
        getMainView().setOnClickListener(this);

        resetHideTimer();
        if (Util.SDK_INT > 23) {
            initPlayer(0);
        }

        isOpen = true;

    }

    protected final View getDecorView() {
        return view.mDecorView;
    }

    protected final View getMainView() {
        return view.mMainView;
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


    public void onResume() {

        if (!view.mbIsPlaying && !view.mIsNotFirstPlay) {
            view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            initSeekBar();
            initPauseButton();
            initPlayButton();

            view.mExoplayer.setPlayWhenReady(true);
            view.mbIsPlaying = true;

        }
        else if (view.mIsNotFirstPlay && !view.mbIsPlaying){
            view.mExoplayer.setPlayWhenReady(true);
            view.mbIsPlaying = true;

            //NEW CODE
            view.mHandler.removeCallbacks(mUpdateTimeTask);
            view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            view.mExoplayer.seekTo(view.currentSeekBarPosition * 1000);
            view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));

            updateSeekBarWhenTouched();

        }

        PresenceSystemAndLastSeen.presenceSystem();

    }

    public void onPause() {
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
        PresenceSystemAndLastSeen.lastSeen();
    }

    public void onStop() {
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }

        isOpen = false;
    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    private void initPlayButton() {
        view.mBtnPlay.setOnClickListener(v -> {
            if (!view.mbIsPlaying && !view.mIsNotFirstPlay) {
                view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
                initPlayer(0);
                initSeekBar();

                view.mExoplayer.setPlayWhenReady(true);
                view.mbIsPlaying = true;
                view.mIsNotFirstPlay = true;
                view.mBtnPlay.setVisibility(GONE);
                view.mBtnPlay.setEnabled(false);
                view.mBtnPause.setVisibility(GONE);
                view.mBtnPause.setEnabled(false);
                view.mCardButtons.setVisibility(GONE);

            }
            else if (view.mIsNotFirstPlay && !view.mbIsPlaying){
                view.mExoplayer.setPlayWhenReady(true);
                view.mbIsPlaying = true;
                view.mIsNotFirstPlay = true;
                view.mBtnPlay.setVisibility(GONE);
                view.mBtnPlay.setEnabled(false);
                view.mBtnPause.setVisibility(GONE);
                view.mBtnPause.setEnabled(false);
                view.mCardButtons.setVisibility(GONE);

                //NEW CODE
                view.mHandler.removeCallbacks(mUpdateTimeTask);
                view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
                view.mExoplayer.seekTo(view.currentSeekBarPosition * 1000);
                view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));

                updateSeekBarWhenTouched();


            }

        });
    }

    private void initPauseButton() {
        view.mBtnPause.setOnClickListener(v -> {
            if (view.mbIsPlaying) {
                view.mBtnPause.setVisibility(GONE);
                view.mBtnPause.setEnabled(false);
                view.mBtnPlay.setVisibility(VISIBLE);
                view.mBtnPlay.setEnabled(true);
                view.mCardButtons.setVisibility(VISIBLE);
                view.mExoplayer.setPlayWhenReady(false);
                view.mbIsPlaying = false;
                view.mIsNotFirstPlay = true;

            }

        });
    }

    private String stringForTime(int timeMs) {
        view.mFormatBuilder = new StringBuilder();
        view.mFormatter = new Formatter(view.mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        view.mFormatBuilder.setLength(0);
        if (hours > 0) {
            return view.mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return view.mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    //NEW CODE TO SET PROGRESS
    private void updateSeekBar(){
        view.mHandler.postDelayed(mUpdateTimeTask, 0);
    }

    public Runnable mUpdateTimeTask  = new Runnable() {
        @Override
        public void run() {
            if (view.mExoplayer != null && view.mbIsPlaying) {
                view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);
                int currentPosition = (int) view.mExoplayer.getCurrentPosition() / 1000;
                view.mSeekBar.setProgress(currentPosition);
                view.mCurrentTime.setText(stringForTime((int) view.mExoplayer.getCurrentPosition()));
                view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));

                view.mHandler.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenTouched(){
        view.mHandlerTouch.postDelayed(mUpdateTimeTaskWhenTouched, 0);
    }

    public Runnable mUpdateTimeTaskWhenTouched  = new Runnable() {
        @Override
        public void run() {
            if (view.mExoplayer != null && view.mbIsPlaying) {

                view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);

                int currentPosition = (int) view.mExoplayer.getCurrentPosition() / 1000;
                view.mSeekBar.setProgress(currentPosition);
                view.mCurrentTime.setText(stringForTime((int) view.mExoplayer.getCurrentPosition()));

                view.mHandlerTouch.postDelayed(this, 0);
            }
            else if (view.mExoplayer != null && !view.mbIsPlaying) {

                view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);

                int currentPosition = (int) view.mExoplayer.getCurrentPosition() / 1000;
                view.mSeekBar.setProgress(currentPosition);
                view.mCurrentTime.setText(stringForTime((int) view.mExoplayer.getCurrentPosition()));

                view.mBtnPlay.setVisibility(GONE);
                view.mBtnPlay.setEnabled(false);
                view.mCardButtons.setVisibility(GONE);
                view.mbIsPlaying = true;

                view.mExoplayer.setPlayWhenReady(true);
                view.mHandlerTouch.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenFinished(){
        view.mHandlerFinished.postDelayed(mUpdateTimeTaskWhenFinished, 0);
    }

    public Runnable mUpdateTimeTaskWhenFinished = new Runnable() {
        @Override
        public void run() {
            if (view.mExoplayer != null) {

                view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);
                view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));

                view.mSeekBar.setProgress(0);
                view.mCurrentTime.setText("00:00");
                if(!view.mbIsPlaying && view.mIsNotFirstPlay){
                    view.mBtnPlay.setVisibility(VISIBLE);
                    view.mBtnPlay.setEnabled(true);
                    view.mCardButtons.setVisibility(VISIBLE);
                    //initPlayButton();
                    view.mBtnPause.setVisibility(GONE);
                    view.mBtnPause.setEnabled(false);
                    view.mBtnPlay.requestFocus();

                }

                view.mHandlerFinished.postDelayed(this, 1000);
            }
        }
    };

    public void initSeekBar() {
        view.mSeekBar.requestFocus();
        view.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                view.currentSeekBarPosition = progress;

                if (!fromUser) {

                    if(((int) view.mExoplayer.getDuration() / 1000) == ((int)view.mExoplayer.getCurrentPosition() / 1000)){
                        //if(((int) view.mExoplayer.getDuration() / 1000) == (view.mSeekBar.getMax())){
                        view.mHandler.removeCallbacks(mUpdateTimeTask);
                        view.mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);

                        view.mIsNotFirstPlay = true;
                        view.mbIsPlaying = false;

                        view.mBtnPause.setVisibility(GONE);
                        view.mCardButtons.setVisibility(GONE);

                        updateSeekBarWhenFinished();
                    }
                    else {
                        //NEW CODE
                        if(((int) view.mExoplayer.getDuration() / 1000) == progress){
                            return;
                        }
                        updateSeekBar();
                        view.mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);
                        view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);

                    }


                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                view.mHandler.removeCallbacks(mUpdateTimeTask);
                view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (view.mExoplayer != null) {

                    //NEW CODE
                    view.mHandler.removeCallbacks(mUpdateTimeTask);
                    view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
                    view.mExoplayer.seekTo(view.currentSeekBarPosition * 1000);
                    view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));
                    view.mBtnPause.setVisibility(GONE);
                    view.mCardButtons.setVisibility(GONE);

                    if (view.mBtnPlay.getVisibility() == VISIBLE){
                        view.mbIsPlaying = false;

                    }
                    else {
                        view.mbIsPlaying = true;


                    }

                    updateSeekBarWhenTouched();
                }

            }
        });
        view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);

    }


    private void initPlayer(int position) {

        //third implementation
        view.mSimpleExoPlayerView.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(view.getContext(), Util.getUserAgent(view.getContext(), "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        view.mExoplayer = ExoPlayerFactory.newSimpleInstance(view.getContext(), trackSelector);
        view.mSimpleExoPlayerView.setPlayer(view.mExoplayer);
        view.mExoplayer.setPlayWhenReady(false);
/*        MediaSource mediaSource = new HlsMediaSource(Uri.parse("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"),
                mediaDataSourceFactory, mainHandler, null);*/

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(view.mUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        view.mExoplayer.addListener(view.mComponentListenter);
        view.mExoplayer.setVideoDebugListener(view.mComponentListenter);
        view.mExoplayer.setAudioDebugListener(view.mComponentListenter);
        view.mExoplayer.prepare(mediaSource);
        view.mExoplayer.seekTo(position);

    }

    public void releasePlayer() {
        if (view.mExoplayer != null) {
            view.playbackPosition = view.mExoplayer.getCurrentPosition();
            view.currentWindow = view.mExoplayer.getCurrentWindowIndex();
            view.playWhenReady = view.mExoplayer.getPlayWhenReady();
            view.mExoplayer.removeListener(view.mComponentListenter);
            view.mExoplayer.setVideoListener(null);
            view.mExoplayer.setVideoDebugListener(null);
            view.mExoplayer.setAudioDebugListener(null);
            view.mHandler.removeCallbacks(mUpdateTimeTask);
            view.mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);
            view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            view.mHandler = null;
            view.mHandlerTouch = null;
            view.mHandlerFinished = null;
            view.mExoplayer.release();
            view.mExoplayer = null;
        }
    }

    //RELEASE PLAYER AND FINISH ACTIVITY - WORKING
    private Subscription observeBtnArrowBack(){
        return view.observableBtnArrowBack()
                .doOnNext(__ -> releasePlayer())
                .subscribe(__ -> {
                    model.backToChatTalkerActivity();
                },
                        Throwable::printStackTrace
                );
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


    private int getMainViewID() {
        return R.id.middle;
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

            view.mToolbarShowVideo.setVisibility(GONE);
            view.mAppBar.setVisibility(GONE);
            view.mRelativeSeekBarTime.setVisibility(GONE);
            view.mCardShowDescription.setVisibility(View.INVISIBLE);

            //view.mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //view.mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            RelativeLayout.LayoutParams paramsAppBar = (RelativeLayout.LayoutParams)view.mAppBar.getLayoutParams();

            setViewMargins(view.mContext, paramsAppBar, 0, 20, 0, 0, view.mAppBar);


            if (view.mbIsPlaying){

                view.mBtnPause.setVisibility(GONE);
                view.mBtnPause.setEnabled(true);
                view.mCardButtons.setVisibility(GONE);

            }

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
        view.mToolbarShowVideo.setVisibility(VISIBLE);
        view.mRelativeSeekBarTime.setVisibility(VISIBLE);
        if (!TextUtils.isEmpty(view.mTvDescription.getText().toString().trim())) {
            view.mCardShowDescription.setVisibility(VISIBLE);
            view.mTvDescription.setVisibility(VISIBLE);
        } else {
            view.mCardShowDescription.setVisibility(View.INVISIBLE);
            view.mTvDescription.setVisibility(View.INVISIBLE);
        }

        if (view.mbIsPlaying){

            view.mBtnPause.setVisibility(VISIBLE);
            view.mCardButtons.setVisibility(VISIBLE);
            view.mBtnPause.setEnabled(true);

        }

        // First cancel any queued events - i.e. resetting the countdown clock
        mLeanBackHandler.removeCallbacks(mEnterLeanback);
        // And fire the event in 3s time
        mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
    }


    //SUBSCRIPTIONS
    private Subscription observeTalker(){
        return model.getTalkerUser(view.mTalkerKey)
                .subscribe(talker -> {
                    setFields(talker);
                            if (talker.getThumb() != null) {
                                setImage(talker.getThumb());
                            }
                            else {
                                setImage(talker.getImageUrl());
                            }

                    if(view.mExoplayer != null){
                        view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));
                    }
                },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeMessage(){
        return model.getMessage(view.mTalkerKey, view.mMessageReference)
                .subscribe(messageUser -> {
                    if (messageUser.getVideo().getDescription() != null){
                        view.mTvDescription.setVisibility(VISIBLE);
                        view.mTvDescription.setText(messageUser.getVideo().getDescription().trim());
                    }
                    else {
                        view.mTvDescription.setVisibility(View.INVISIBLE);
                    }
                },
                        Throwable::printStackTrace
                );
    }

    //SET NAME'S TRIBU
    private void setFields(User talker){
        view.mTalkerName.setText(talker.getName());
        view.mUsenameTalker.setText(talker.getUsername());
    }

    //SETTING IMAGE
    private void setImage(String imageUrl){
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
                .setUri(Uri.parse(imageUrl))
                .setControllerListener(listener)
                .setOldController(view.mCircleTalkerImage.getController())
                .build();
        view.mCircleTalkerImage.setController(dc);

    }




    public void onDestroy(){
        subscription.clear();
    }
}
