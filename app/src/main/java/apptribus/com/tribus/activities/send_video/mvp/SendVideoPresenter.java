package apptribus.com.tribus.activities.send_video.mvp;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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

import apptribus.com.tribus.util.CacheDataSourceFactory;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 7/12/2017.
 */

public class SendVideoPresenter implements View.OnClickListener{

    private final SendVideoView view;
    private final SendVideoModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private String description;
    private boolean response;
    public static boolean isOpen;



    //REFERENCES - FIREBASE (KEEP SYNCED)
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);

    private final Handler mLeanBackHandler = new Handler();
    private int mLastSystemUIVisibility;
    private final Runnable mEnterLeanback = () -> enableFullScreen(true);


    public SendVideoPresenter(SendVideoView view, SendVideoModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart(){
        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mReferenceUser.keepSynced(true);
        mRefTribusMessage.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();

        view.mBtnSendMessage.setEnabled(true);

        subscription.add(observeTribu());
        subscription.add(observeEtDescription());
        subscription.add(observeBtnSendVideo());
        subscription.add(observeBtnArrowBack());

        if (view.mHandler == null) {
            view.mHandler = new Handler();
        }
        if (view.mHandlerTouch == null) {
            view.mHandlerTouch = new Handler();
        }

        if (view.mHandlerFinished == null) {
            view.mHandlerFinished = new Handler();
        }
        view.mIsNotFirstPlay = false;

        if (Util.SDK_INT > 23) {
            initPlayer(0);
        }

        view.mRelativeMiddle.setOnClickListener(this);

        isOpen = true;
    }

    public void onRestart(){
        PresenceSystemAndLastSeen.presenceSystem();
    }

    public void onResume() {

        if (!view.mbIsPlaying && !view.mIsNotFirstPlay) {
            view.mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            initSeekBar();
            initPauseButton();
            initPlayButton();

            view.mExoplayer.setPlayWhenReady(false);
            //view.mbIsPlaying = true;

        } else if (view.mIsNotFirstPlay && !view.mbIsPlaying) {
            view.mExoplayer.setPlayWhenReady(false);
            //view.mbIsPlaying = true;

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

            } else if (view.mIsNotFirstPlay && !view.mbIsPlaying) {
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
    private void updateSeekBar() {
        view.mHandler.postDelayed(mUpdateTimeTask, 0);
    }

    public Runnable mUpdateTimeTask = new Runnable() {
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

    private void updateSeekBarWhenTouched() {
        view.mHandlerTouch.postDelayed(mUpdateTimeTaskWhenTouched, 0);
    }

    public Runnable mUpdateTimeTaskWhenTouched = new Runnable() {
        @Override
        public void run() {
            if (view.mExoplayer != null && view.mbIsPlaying) {

                view.mSeekBar.setMax((int) view.mExoplayer.getDuration() / 1000);

                int currentPosition = (int) view.mExoplayer.getCurrentPosition() / 1000;
                view.mSeekBar.setProgress(currentPosition);
                view.mCurrentTime.setText(stringForTime((int) view.mExoplayer.getCurrentPosition()));

                view.mHandlerTouch.postDelayed(this, 0);
            } else if (view.mExoplayer != null && !view.mbIsPlaying) {

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

    private void updateSeekBarWhenFinished() {
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
                if (!view.mbIsPlaying && view.mIsNotFirstPlay) {
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


                    if (((int) view.mExoplayer.getDuration() / 1000) == ((int) view.mExoplayer.getCurrentPosition() / 1000)) {
                        //if(((int) view.mExoplayer.getDuration() / 1000) == (view.mSeekBar.getMax())){
                        view.mHandler.removeCallbacks(mUpdateTimeTask);
                        view.mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);


                        view.mIsNotFirstPlay = true;
                        view.mbIsPlaying = false;

                        view.mBtnPause.setVisibility(GONE);
                        view.mCardButtons.setVisibility(GONE);

                        updateSeekBarWhenFinished();
                    } else {
                        //NEW CODE
                        if (((int) view.mExoplayer.getDuration() / 1000) == progress) {

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

                    if (view.mBtnPlay.getVisibility() == VISIBLE) {
                        view.mbIsPlaying = false;

                    } else {
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


        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        view.mExoplayer = ExoPlayerFactory.newSimpleInstance(view.getContext(), trackSelector);

        //CACHE
        MediaSource videoSource = new ExtractorMediaSource(view.mUri,
                new CacheDataSourceFactory(view.mContext, 100 * 1024 * 1024, 5 * 1024 * 1024),
                new DefaultExtractorsFactory(), null, null);

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
        view.mExoplayer.prepare(videoSource);
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

    private void enableFullScreen(boolean enabled) {

        if (enabled) {
            if (view.mbIsPlaying) {

                view.mBtnPause.setVisibility(GONE);
                view.mBtnPause.setEnabled(true);
                view.mCardButtons.setVisibility(GONE);

            }

        }

        // Want to hide again after 3s
        if (!enabled) {
            resetHideTimer();
        }

    }

    private void resetHideTimer() {
        if (view.mbIsPlaying) {

            view.mBtnPause.setVisibility(VISIBLE);
            view.mCardButtons.setVisibility(VISIBLE);
            view.mBtnPause.setEnabled(true);

        }

        // First cancel any queued events - i.e. resetting the countdown clock
        mLeanBackHandler.removeCallbacks(mEnterLeanback);
        // And fire the event in 3s time
        mLeanBackHandler.postDelayed(mEnterLeanback, 3000);
    }


    //RELEASE PLAYER AND FINISH ACTIVITY - WORKING
    private Subscription observeBtnArrowBack() {
        return view.observableBtnArrowBack()
                .doOnNext(__ -> releasePlayer())
                .subscribe(__ -> {
                            if (view.mCameFrom != null) {
                                if (view.mCameFrom.equals("fromCamActivityChatTribu")) {
                                    model.backToCam(view.mTribuKey, view.mTopicKey);
                                } else if (view.mCameFrom.equals("fromShareActivity")) {
                                    model.backToChatTribu(view.mTribuKey, view.mTopicKey);
                                }
                            } else {
                                model.backToChatActivity();
                            }
                        },
                        Throwable::printStackTrace
                );
    }



    //SUBSCRIPTIONS
    private Subscription observeEtDescription(){
        return view.observeEtDescription()
                .subscribe(__ -> {
                    view.mBtnPause.setVisibility(View.GONE);
                    view.mBtnPause.setEnabled(false);
                    view.mBtnPlay.setVisibility(VISIBLE);
                    //view.mbIsPlaying = false;
                    releasePlayer();
                    initSeekBar();
                },
                        Throwable::printStackTrace
                );

    }

    private Subscription observeTribu(){
        return model.getTribu(view.mTribuKey)
                .subscribe(tribu -> {
                    setFields(tribu);
                    setImage(tribu);
                    if(view.mExoplayer != null){
                        view.mTotalDuration.setText(stringForTime((int) view.mExoplayer.getDuration()));
                    }
                },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnSendVideo(){
        return view.observeBtnSendVideo()
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
                .doOnNext(__ -> view.mBtnSendMessage.setEnabled(false))
                .doOnNext(__ -> getDescription())
                .switchMap(user -> model.getUser())
                .doOnNext(user -> model.uploadVideoToFirebase(user, view.mUri, description,
                        view.mTotalDuration.getText().toString().trim(), view.mFileSize, view.mTopicKey, view.mTribuKey))
                .subscribe();
    };

    private void getDescription(){
        description = view.mEtDescription.getText().toString().trim();
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


    public void onDestroy(){
        subscription.clear();
    }

    @Override
    public void onClick(View v) {
        resetHideTimer();
    }
}
