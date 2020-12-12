package apptribus.com.tribus.activities.chat_user.view_holder;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import apptribus.com.tribus.R;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by User on 6/11/2017.
 */

public class UserMessagePrivateViewHolder extends RecyclerView.ViewHolder{

    public Context mContext;

    //--------------------------------------------- WIDGETS USER LEFT -------------------------------------------------------------
    //text message
    @BindView(R.id.include_layout_text_message_user_left_private_chat)
    public View mIncludeLayoutTextMessageUserLeft;

    @BindView(R.id.tv_message_user_left)
    public TextView mTvMessageUserLeft;

    @BindView(R.id.message_time_user_left)
    public TextView mTvMessageTimeUserLeft;


    //voice message
    @BindView(R.id.include_layout_audio_message_user_left_private_chat)
    public View mIncludeLayoutAudioMessageUserLeft;

    @BindView(R.id.img_btn_play_user_left)
    public ImageButton mBtnPlayAudioUserLeft;

    @BindView(R.id.img_btn_pause_user_left)
    public ImageButton mBtnPauseAudioUserLeft;

    @BindView(R.id.tv_audio_start_user_left)
    public TextView mTvAudioStartUserLeft;

    @BindView(R.id.tv_audio_end_user_left)
    public TextView mTvAudioEndUserLeft;

    @BindView(R.id.message_time_audio_user_left)
    public TextView mTvMessageTimeAudioUserLeft;

    @BindView(R.id.seek_bar_voice_message_user_left)
    public SeekBar mSeekBarVoiceMessageUserLeft;

    @BindView(R.id.progress_loading_audio)
    public ProgressBar mProgressLoadingAudio;

    @BindView(R.id.iv_clock)
    public ImageView mIvClock;


    //video message
    @BindView(R.id.include_layout_video_message_user_left_private_chat)
    public View mIncludeLayoutVideoMessageUserLeft;

    @BindView(R.id.video_frame_user_left)
    public SimpleExoPlayerView mSimplePlayerUserLeft;

    @BindView(R.id.progress_user_left)
    public ProgressBar mProgressVideoUserLeft;

    @BindView(R.id.loading_painel_user_left)
    public RelativeLayout mRelativeLoadingPanelUserLeft;

    @BindView(R.id.btn_play_user_left)
    public ImageButton mBtnPlayVideoUserLeft;

    @BindView(R.id.btn_download_user_left)
    public ImageButton mBtnDownloadVideoUserLeft;

    @BindView(R.id.tv_duration_user_left)
    public TextView mTvVideoDurationUserLeft;

    @BindView(R.id.linear_video_info_user_left)
    public LinearLayout mLinearVideoInformationUserLeft;

    @BindView(R.id.linear_video_description_user_left)
    public RelativeLayout mRelativeVideoDescriptionUserLeft;

    @BindView(R.id.tv_video_description_user_left)
    public TextView mTvVideoDescriptionUserLeft;

    @BindView(R.id.message_time_video_user_left)
    public TextView mTvMessageTimeVideoUserLeft;

    @BindView(R.id.relative_exoplayer_view)
    public RelativeLayout mRelativeSimpleExoplayer;


    //image message
    @BindView(R.id.include_layout_image_message_user_left_private_chat)
    public View mIncludeLayoutImageMessageUserLeft;

    @BindView(R.id.relative_simple_drawee_view_user_left)
    public RelativeLayout mRelativeSimpleDraweeUserLeft;

    @BindView(R.id.image_frame_user_left)
    public SimpleDraweeView mSdImageFrameUserLeft;

    @BindView(R.id.loading_painel_image_user_left)
    public RelativeLayout mRelativeLoadingPanelImageUserLeft;

    @BindView(R.id.progress_image_user_letf)
    public ProgressBar mProgressImageUserLeft;

    @BindView(R.id.btn_download_image_user_left)
    public ImageButton mBtnDownloadImageUserLeft;

    @BindView(R.id.linear_image_info_user_left)
    public LinearLayout mLinearImageInfoUserLeft;


    @BindView(R.id.relative_image_description_user_left)
    public RelativeLayout mRelativeImageDescriptionUserLeft;

    @BindView(R.id.tv_image_description_user_left)
    public TextView mTvImageDescriptionUserLeft;

    @BindView(R.id.message_time_image_user_left)
    public TextView mTvMessageTimeImageUserLeft;



    //--------------------------------------------- WIDGETS USER RIGHT -------------------------------------------------------------
    //text message
    @BindView(R.id.include_layout_text_message_user_right_private_chat)
    public View mIncludeLayoutTextMessageUserRight;

    @BindView(R.id.tv_message_user_right)
    public TextView mTvMessageUserRight;

    @BindView(R.id.circle_user_image_right)
    public SimpleDraweeView mCircleImageUserRight;

    @BindView(R.id.message_time_user_right)
    public TextView mTvMessageTimeUserRight;

    @BindView(R.id.tv_username)
    TextView mTvUserName;


    //voice message
    @BindView(R.id.include_layout_audio_message_user_right_private_chat)
    public View mIncludeLayoutAudioMessageUserRight;

    @BindView(R.id.img_btn_play_user_right)
    public ImageButton mBtnPlayAudioUserRight;

    @BindView(R.id.img_btn_pause_user_right)
    public ImageButton mBtnPauseAudioUserRight;

    @BindView(R.id.tv_audio_start_user_right)
    public TextView mTvAudioStartUserRight;

    @BindView(R.id.tv_audio_end_user_right)
    public TextView mTvAudioEndUserRight;

    @BindView(R.id.message_time_audio_user_right)
    public TextView mTvMessageTimeAudioUserRight;

    @BindView(R.id.seek_bar_voice_message_user_right)
    public SeekBar mSeekBarVoiceMessageUserRight;

    @BindView(R.id.circle_user_image_right_voice_message)
    public SimpleDraweeView mCircleImageUserRightAudioMessage;

    @BindView(R.id.tv_username_audio)
    public TextView mTvUserNameVoice;


    //video messages
    @BindView(R.id.include_layout_video_message_user_right_private_chat)
    public View mIncludeLayoutVideoMessageUserRight;

    @BindView(R.id.circle_user_image_right_video_message)
    public SimpleDraweeView mCircleImageUserRightVideoMessage;

    @BindView(R.id.video_frame_user_right)
    public SimpleExoPlayerView mSimplePlayerUserRight;

    @BindView(R.id.progress_user_right)
    public ProgressBar mProgressVideoUserRight;

    @BindView(R.id.loading_painel_user_right)
    public RelativeLayout mRelativeLoadingPanelUserRight;

    @BindView(R.id.btn_play_user_right)
    public ImageButton mBtnPlayVideoUserRight;

    @BindView(R.id.btn_download_user_right)
    public ImageButton mBtnDownloadVideoUserRight;

    @BindView(R.id.tv_size_user_right)
    public TextView mTvVideoSizeUserRight;

    @BindView(R.id.tv_duration_user_right)
    public TextView mTvVideoDurationUserRight;

    @BindView(R.id.linear_video_info_user_right)
    public LinearLayout mLinearVideoInformationUserRight;

    @BindView(R.id.linear_video_description_user_right)
    public RelativeLayout mRelativeVideoDescriptionUserRight;

    @BindView(R.id.tv_video_description_user_right)
    public TextView mTvVideoDescriptionUserRight;

    @BindView(R.id.message_time_video_user_right)
    public TextView mTvMessageTimeVideoUserRight;

    @BindView(R.id.relative_exoplayer_view_user_right)
    public RelativeLayout mRelativeSimpleExoplayerUserRight;

    @BindView(R.id.tv_username_video)
    public TextView mTvUserNameVideo;


    //image message
    @BindView(R.id.include_layout_image_message_user_right_private_chat)
    public View mIncludeLayoutImageMessageUserRight;

    @BindView(R.id.relative_simple_drawee_view_user_right)
    public RelativeLayout mRelativeSimpleDraweeUserRight;

    @BindView(R.id.image_frame_user_right)
    public SimpleDraweeView mSdImageFrameUserRight;

    @BindView(R.id.circle_user_image_right_image_message)
    public SimpleDraweeView mCircleImageUserRightImageMessage;

    @BindView(R.id.loading_painel_image_user_right)
    public RelativeLayout mRelativeLoadingPanelImageUserRight;

    @BindView(R.id.progress_image_user_right)
    public ProgressBar mProgressImageUserRight;

    @BindView(R.id.btn_download_image_user_right)
    public ImageButton mBtnDownloadImageUserRight;

    @BindView(R.id.linear_image_info_user_right)
    public LinearLayout mLinearImageInfoUserRight;

    @BindView(R.id.tv_size_image_user_right)
    public TextView mTvImageSizeUserRight;

    @BindView(R.id.relative_image_description_user_right)
    public RelativeLayout mRelativeImageDescriptionUserRight;

    @BindView(R.id.tv_image_description_user_right)
    public TextView mTvImageDescriptionUserRight;

    @BindView(R.id.message_time_image_user_right)
    public TextView mTvMessageTimeImageUserRight;

    @BindView(R.id.tv_username_image)
    public TextView mTvUserNameImage;


    //--------------------------------------------- VARIABLES -------------------------------------------------------------
    //USER LEFT
    public SimpleExoPlayer mExoplayer;
    public boolean mbIsPlaying = false;
    public StringBuilder mFormatBuilder;
    public Formatter mFormatter;
    public Handler mHandler;
    public boolean mIsFirstPlay;
    public int currentSeekBarPosition;
    public Handler mHandlerTouch;
    public int mTimeFinished;
    public Handler mHandlerFinished;
    public BandwidthMeter mBandwidthMeter;
    public long playbackPosition;
    public int currentWindow;
    public boolean playWhenReady;


    //USER RIGHT
    public SimpleExoPlayer mExoplayerUserRight;
    public boolean mbIsPlayingUserRight = false;
    public StringBuilder mFormatBuilderUserRight;
    public Formatter mFormatterUserRight;
    public Handler mHandlerUserRight;
    public boolean mIsFirstPlayUserRight;
    public int currentSeekBarPositionUserRight;
    public Handler mHandlerTouchUserRight;
    public int mTimeFinishedUserRight;
    public Handler mHandlerFinishedUserRight;
    public BandwidthMeter mBandwidthMeterUserRight;
    public long playbackPositionUserRight;
    public int currentWindowUserRight;
    public boolean playWhenReadyUserRight;



    public UserMessagePrivateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();

        if (mExoplayer != null) {
            playbackPosition = mExoplayer.getCurrentPosition();
            currentWindow = mExoplayer.getCurrentWindowIndex();
            playWhenReady = mExoplayer.getPlayWhenReady();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);
            mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            mHandler = null;
            mHandlerTouch = null;
            mHandlerFinished = null;
            mExoplayer.release();
            mExoplayer = null;
        }

        if (mExoplayerUserRight != null) {
            playbackPositionUserRight = mExoplayerUserRight.getCurrentPosition();
            currentWindowUserRight = mExoplayerUserRight.getCurrentWindowIndex();
            playWhenReadyUserRight = mExoplayerUserRight.getPlayWhenReady();
            mHandlerUserRight.removeCallbacks(mUpdateTimeTaskUserRight);
            mHandlerTouchUserRight.removeCallbacks(mUpdateTimeTaskWhenTouchedUserRight);
            mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);
            mHandlerUserRight = null;
            mHandlerTouchUserRight = null;
            mHandlerFinishedUserRight = null;
            mExoplayerUserRight.release();
            mExoplayerUserRight = null;
        }
    }


    //--------------------------------------------- CODE USER LEFT -------------------------------------------------------------

    // TEXT MESSAGE
    //set message
    public void setTvMessageUserLeft(String message) {
        mTvMessageUserLeft.setText(message);
    }

    public void setTvMessageLinkUserLeft(SpannableString message) {
        mTvMessageUserLeft.setText(message);
    }

    //set time text message
    public void setTvMessageTimeUserLeft(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeUserLeft.setText(time);
    }


    // VOICE MESSAGE
    //set time voice message
    public void setTvMessageTimeAudioUserLeft(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM");
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeAudioUserLeft.setText(time);
    }

    public void setAudioDurationUserLeft(String duration){
        mTvAudioEndUserLeft.setText(duration);
    }

    public void initPlayer(int position, Uri uri) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoplayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, extractorsFactory, null, null);

        mExoplayer.prepare(mediaSource);
        mTvAudioStartUserLeft.setText("00:00");
        //mTvAudioEndUserLeft.setText(stringForTime((int) mExoplayer.getDuration()));

        mExoplayer.setPlayWhenReady(false);
        mExoplayer.seekTo(position);

        if (mHandler == null) {
            mHandler = new Handler();
        }
        if (mHandlerTouch == null) {
            mHandlerTouch = new Handler();
        }

        if (mHandlerFinished == null) {
            mHandlerFinished = new Handler();
        }

        mIsFirstPlay = true;
        mbIsPlaying = false;

        mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
        mBtnPlayAudioUserLeft.setEnabled(true);
        mBtnPauseAudioUserLeft.setVisibility(GONE);
        mBtnPauseAudioUserLeft.setEnabled(false);

    }

    public void initPlayButton() {
        if (mIsFirstPlay) {
            mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            initSeekBar();
            mExoplayer.setPlayWhenReady(true);
            mBtnPlayAudioUserLeft.setVisibility(GONE);
            mBtnPlayAudioUserLeft.setEnabled(false);
            mBtnPauseAudioUserLeft.setVisibility(VISIBLE);
            mBtnPauseAudioUserLeft.setEnabled(true);
            mbIsPlaying = true;

        }
        else {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
            mbIsPlaying = true;
            mExoplayer.seekTo(currentSeekBarPosition * 1000);
            mTvAudioEndUserLeft.setText(stringForTime((int) mExoplayer.getDuration()));

            updateSeekBarWhenTouched();

        }

    }

    public void initPauseButton() {
        mBtnPauseAudioUserLeft.setVisibility(GONE);
        mBtnPauseAudioUserLeft.setEnabled(false);
        mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
        mBtnPlayAudioUserLeft.setEnabled(true);
        mExoplayer.setPlayWhenReady(false);
        mbIsPlaying = false;
    }

    private String stringForTime(int timeMs) {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void updateSeekBar() {
        mHandler.postDelayed(mUpdateTimeTask, 0);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (mExoplayer != null) {

                mSeekBarVoiceMessageUserLeft.setMax((int) mExoplayer.getDuration() / 1000);
                int currentPosition = (int) mExoplayer.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserLeft.setProgress(currentPosition);
                mTvAudioStartUserLeft.setText(stringForTime((int) mExoplayer.getCurrentPosition()));
                mTvAudioEndUserLeft.setText(stringForTime((int) mExoplayer.getDuration()));

                mHandler.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenTouched() {
        mHandlerTouch.postDelayed(mUpdateTimeTaskWhenTouched, 0);
    }

    private Runnable mUpdateTimeTaskWhenTouched = new Runnable() {
        @Override
        public void run() {

            if (mExoplayer != null && mbIsPlaying) {
                mSeekBarVoiceMessageUserLeft.setMax((int) mExoplayer.getDuration() / 1000);

                int currentPosition = (int) mExoplayer.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserLeft.setProgress(currentPosition);
                mTvAudioStartUserLeft.setText(stringForTime((int) mExoplayer.getCurrentPosition()));

                //mHandlerTouch.postDelayed(this, 0);
                mBtnPlayAudioUserLeft.setVisibility(GONE);
                mBtnPlayAudioUserLeft.setEnabled(false);
                mBtnPauseAudioUserLeft.setVisibility(VISIBLE);
                mBtnPauseAudioUserLeft.setEnabled(true);

                mExoplayer.setPlayWhenReady(true);

                mHandlerTouch.postDelayed(this, 0);

            }
            else if(mExoplayer != null && !mbIsPlaying){

                mSeekBarVoiceMessageUserLeft.setMax((int) mExoplayer.getDuration() / 1000);

                int currentPosition = (int) mExoplayer.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserLeft.setProgress(currentPosition);
                mTvAudioStartUserLeft.setText(stringForTime((int) mExoplayer.getCurrentPosition()));

                mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
                mBtnPlayAudioUserLeft.setEnabled(true);
                mBtnPauseAudioUserLeft.setVisibility(GONE);
                mBtnPauseAudioUserLeft.setEnabled(false);

                mExoplayer.setPlayWhenReady(false);
                mHandlerTouch.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenFinished() {
        mHandlerFinished.postDelayed(mUpdateTimeTaskWhenFinished, 0);
    }

    private Runnable mUpdateTimeTaskWhenFinished = new Runnable() {
        @Override
        public void run() {
            if (mExoplayer != null) {

                mSeekBarVoiceMessageUserLeft.setMax((int) mExoplayer.getDuration() / 1000);
                mTvAudioEndUserLeft.setText(stringForTime((int) mExoplayer.getDuration()));

                mSeekBarVoiceMessageUserLeft.setProgress(0);
                mTvAudioStartUserLeft.setText("00:00");

                if (mbIsPlaying && mIsFirstPlay) {
                    mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
                    mBtnPlayAudioUserLeft.setEnabled(true);
                    //mCardButtons.setVisibility(VISIBLE);
                    //initPlayButton();
                    mBtnPauseAudioUserLeft.setVisibility(GONE);
                    mBtnPauseAudioUserLeft.setEnabled(false);
                    mBtnPlayAudioUserLeft.requestFocus();

                    mIsFirstPlay = false;
                    mbIsPlaying = false;
                }

                mHandlerFinished.postDelayed(this, 1000);
            }
        }
    };

    private void initSeekBar() {

        mSeekBarVoiceMessageUserLeft.requestFocus();
        mSeekBarVoiceMessageUserLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currentSeekBarPosition = progress;

                if (!fromUser) {
                    if (((int) mExoplayer.getDuration() / 1000) == ((int) mExoplayer.getCurrentPosition() / 1000)) {
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);

                        updateSeekBarWhenFinished();
                    }
                    else {
                        if (((int) mExoplayer.getDuration() / 1000) == progress) {

                            return;
                        }

                        updateSeekBar();

                        mHandlerTouch.removeCallbacks(mUpdateTimeTaskWhenTouched);
                        mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mExoplayer != null) {

                    mHandler.removeCallbacks(mUpdateTimeTask);
                    mHandlerFinished.removeCallbacks(mUpdateTimeTaskWhenFinished);
                    mExoplayer.seekTo(currentSeekBarPosition * 1000);
                    mTvAudioEndUserLeft.setText(stringForTime((int) mExoplayer.getDuration()));

                    mbIsPlaying = false;

                    updateSeekBarWhenTouched();
                }

            }
        });
        mSeekBarVoiceMessageUserLeft.setMax((int) mExoplayer.getDuration() / 1000);

    }

    // VIDEO MESSAGE
    /*public void setTvSizeLeft(String size){

        String appendSize = size.substring(0, 1) + " MB";

        mTvVideoSizeUserLeft.setText(appendSize);
    }*/

    public void setTvDurationLeft(String duration){
        mTvVideoDurationUserLeft.setText(duration);
    }

    public void setTvDescriptionLeft(String description){
        mTvVideoDescriptionUserLeft.setText(description);
    }

    public void setTvMessageTimeVideoUserLeft(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM");
        String time = sfd.format(new Date(timestamp));

        //String appendTime = "enviada: " + time;
        //Log.d("Valor: ", "appendTime: " + appendTime);

        /*SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        String time = sfd.format(new Date(timestamp));*/

        mTvMessageTimeVideoUserLeft.setText(time);
    }

    // IMAGE MESSAGE
    /*public void setTvSizeImageLeft(String size){

        String newSize = size;
        if(size.length() >= 2 && String.valueOf(newSize).substring(0, 1).equals("0")){

            String format = size
                    .substring(1, 2);

            String appendSize = format + " KB";
            mTvImageSizeUserLeft.setText(appendSize);
        }
        else {
            String appendSize = size.substring(0, 1) + " MB";

            mTvImageSizeUserLeft.setText(appendSize);
        }


    }*/

    public void setTvImageDescriptionLeft(String description){
        mTvImageDescriptionUserLeft.setText(description);
    }

    public void setTvMessageTimeImageUserLeft(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM");
        String time = sfd.format(new Date(timestamp));

        //String appendTime = "enviada: " + time;
        //Log.d("Valor: ", "appendTime: " + appendTime);

        /*SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        String time = sfd.format(new Date(timestamp));*/

        mTvMessageTimeImageUserLeft.setText(time);
    }



    //--------------------------------------------- CODE USER RIGHT -------------------------------------------------------------

    //TEXT MESSAGE
    //set message
    public void setTvMessageUserRight(String message) {
        mTvMessageUserRight.setText(message);
    }

    public void setTvMessageLinkUserRight(SpannableString message) {
        mTvMessageUserRight.setText(message);
    }

    //set time text message
    public void setTvMessageTimeUserRight(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeUserRight.setText(time);
    }


    //VOICE MESSAGE
    //set time voice message
    public void setTvMessageTimeAudioUserRight(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeAudioUserRight.setText(time);
    }

    public void setAudioDurationUserRight(String duration){
        mTvAudioEndUserRight.setText(duration);
    }


    public void initPlayerUserRight(int position, Uri uri) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoplayerUserRight = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(uri,
                mediaDataSourceFactory, extractorsFactory, null, null);

        mExoplayerUserRight.prepare(mediaSource);
        mTvAudioStartUserRight.setText("00:00");
        //mTvAudioEndUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getDuration()));

        mExoplayerUserRight.setPlayWhenReady(false);
        mExoplayerUserRight.seekTo(position);

        if (mHandlerUserRight == null) {
            mHandlerUserRight = new Handler();
        }
        if (mHandlerTouchUserRight == null) {
            mHandlerTouchUserRight = new Handler();
        }

        if (mHandlerFinishedUserRight == null) {
            mHandlerFinishedUserRight = new Handler();
        }

        mIsFirstPlayUserRight = true;
        mbIsPlayingUserRight = false;

        mBtnPlayAudioUserRight.setVisibility(VISIBLE);
        mBtnPlayAudioUserRight.setEnabled(true);
        mBtnPauseAudioUserRight.setVisibility(GONE);
        mBtnPauseAudioUserRight.setEnabled(false);

    }

    public void initPlayButtonUserRight() {
        if (mIsFirstPlayUserRight) {
            mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);
            initSeekBarUserRight();
            mExoplayerUserRight.setPlayWhenReady(true);
            mBtnPlayAudioUserRight.setVisibility(GONE);
            mBtnPlayAudioUserRight.setEnabled(false);
            mBtnPauseAudioUserRight.setVisibility(VISIBLE);
            mBtnPauseAudioUserRight.setEnabled(true);
            mbIsPlayingUserRight = true;

        }
        else {
            mHandlerUserRight.removeCallbacks(mUpdateTimeTaskUserRight);
            mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);
            mbIsPlayingUserRight = true;
            mExoplayerUserRight.seekTo(currentSeekBarPositionUserRight * 1000);
            mTvAudioEndUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getDuration()));

            updateSeekBarWhenTouchedUserRight();

        }

    }

    public void initPauseButtonUserRight() {
        mBtnPauseAudioUserRight.setVisibility(GONE);
        mBtnPauseAudioUserRight.setEnabled(false);
        mBtnPlayAudioUserRight.setVisibility(VISIBLE);
        mBtnPlayAudioUserRight.setEnabled(true);
        mExoplayerUserRight.setPlayWhenReady(false);
        mbIsPlayingUserRight = false;
    }

    private String stringForTimeUserRight(int timeMs) {
        mFormatBuilderUserRight = new StringBuilder();
        mFormatterUserRight = new Formatter(mFormatBuilderUserRight, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilderUserRight.setLength(0);
        if (hours > 0) {
            return mFormatterUserRight.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatterUserRight.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void updateSeekBarUserRight() {
        mHandlerUserRight.postDelayed(mUpdateTimeTaskUserRight, 0);
    }

    private Runnable mUpdateTimeTaskUserRight = new Runnable() {
        @Override
        public void run() {
            if (mExoplayerUserRight != null) {

                mSeekBarVoiceMessageUserRight.setMax((int) mExoplayerUserRight.getDuration() / 1000);
                int currentPosition = (int) mExoplayerUserRight.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserRight.setProgress(currentPosition);
                mTvAudioStartUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getCurrentPosition()));
                mTvAudioEndUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getDuration()));

                mHandlerUserRight.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenTouchedUserRight() {
        mHandlerTouchUserRight.postDelayed(mUpdateTimeTaskWhenTouchedUserRight, 0);
    }

    private Runnable mUpdateTimeTaskWhenTouchedUserRight = new Runnable() {
        @Override
        public void run() {

            if (mExoplayerUserRight != null && mbIsPlayingUserRight) {
                mSeekBarVoiceMessageUserRight.setMax((int) mExoplayerUserRight.getDuration() / 1000);

                int currentPosition = (int) mExoplayerUserRight.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserRight.setProgress(currentPosition);
                mTvAudioStartUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getCurrentPosition()));

                //mHandlerTouch.postDelayed(this, 0);
                mBtnPlayAudioUserRight.setVisibility(GONE);
                mBtnPlayAudioUserRight.setEnabled(false);
                mBtnPauseAudioUserRight.setVisibility(VISIBLE);
                mBtnPauseAudioUserRight.setEnabled(true);

                mExoplayerUserRight.setPlayWhenReady(true);

                mHandlerTouchUserRight.postDelayed(this, 0);

            }
            else if(mExoplayerUserRight != null && !mbIsPlayingUserRight){

                mSeekBarVoiceMessageUserRight.setMax((int) mExoplayerUserRight.getDuration() / 1000);

                int currentPosition = (int) mExoplayerUserRight.getCurrentPosition() / 1000;
                mSeekBarVoiceMessageUserRight.setProgress(currentPosition);
                mTvAudioStartUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getCurrentPosition()));

                mBtnPlayAudioUserRight.setVisibility(VISIBLE);
                mBtnPlayAudioUserRight.setEnabled(true);
                mBtnPauseAudioUserRight.setVisibility(GONE);
                mBtnPauseAudioUserRight.setEnabled(false);

                mExoplayerUserRight.setPlayWhenReady(false);
                mHandlerTouchUserRight.postDelayed(this, 0);
            }
        }
    };

    private void updateSeekBarWhenFinishedUserRight() {
        mHandlerFinishedUserRight.postDelayed(mUpdateTimeTaskWhenFinishedUserRight, 0);
    }

    private Runnable mUpdateTimeTaskWhenFinishedUserRight = new Runnable() {
        @Override
        public void run() {
            if (mExoplayerUserRight != null) {

                mSeekBarVoiceMessageUserRight.setMax((int) mExoplayerUserRight.getDuration() / 1000);
                mTvAudioEndUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getDuration()));

                mSeekBarVoiceMessageUserRight.setProgress(0);
                mTvAudioStartUserRight.setText("00:00");

                if (mbIsPlayingUserRight && mIsFirstPlayUserRight) {
                    mBtnPlayAudioUserRight.setVisibility(VISIBLE);
                    mBtnPlayAudioUserRight.setEnabled(true);
                    mBtnPauseAudioUserRight.setVisibility(GONE);
                    mBtnPauseAudioUserRight.setEnabled(false);
                    mBtnPlayAudioUserRight.requestFocus();

                    mIsFirstPlayUserRight = false;
                    mbIsPlayingUserRight = false;
                }

                mHandlerFinishedUserRight.postDelayed(this, 1000);
            }
        }
    };

    private void initSeekBarUserRight() {

        mSeekBarVoiceMessageUserRight.requestFocus();
        mSeekBarVoiceMessageUserRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                currentSeekBarPositionUserRight = progress;

                if (!fromUser) {
                    if (((int) mExoplayerUserRight.getDuration() / 1000) == ((int) mExoplayerUserRight.getCurrentPosition() / 1000)) {
                        mHandlerUserRight.removeCallbacks(mUpdateTimeTaskUserRight);
                        mHandlerTouchUserRight.removeCallbacks(mUpdateTimeTaskWhenTouchedUserRight);

                        updateSeekBarWhenFinishedUserRight();
                    }
                    else {
                        if (((int) mExoplayerUserRight.getDuration() / 1000) == progress) {

                            return;
                        }

                        updateSeekBarUserRight();

                        mHandlerTouchUserRight.removeCallbacks(mUpdateTimeTaskWhenTouchedUserRight);
                        mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mHandlerUserRight.removeCallbacks(mUpdateTimeTaskUserRight);
                mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mExoplayerUserRight != null) {

                    mHandlerUserRight.removeCallbacks(mUpdateTimeTaskUserRight);
                    mHandlerFinishedUserRight.removeCallbacks(mUpdateTimeTaskWhenFinishedUserRight);
                    mExoplayerUserRight.seekTo(currentSeekBarPositionUserRight * 1000);
                    mTvAudioEndUserRight.setText(stringForTimeUserRight((int) mExoplayerUserRight.getDuration()));

                    mbIsPlayingUserRight = false;

                    updateSeekBarWhenTouchedUserRight();
                }

            }
        });
        mSeekBarVoiceMessageUserRight.setMax((int) mExoplayerUserRight.getDuration() / 1000);

    }


    // VIDEO MESSAGE
    public void setTvDurationRight(String duration){
        mTvVideoDurationUserRight.setText(duration);
    }

    public void setTvDescriptionRight(String description){
        mTvVideoDescriptionUserRight.setText(description);
    }

    public void setTvMessageTimeVideoUserRight(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeVideoUserRight.setText(time);
    }



    //IMAGE MESSAGE
    public void setTvImageDescriptionRight(String description){
        mTvImageDescriptionUserRight.setText(description);
    }

    public void setTvMessageTimeImageUserRight(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeImageUserRight.setText(time);
    }


}
