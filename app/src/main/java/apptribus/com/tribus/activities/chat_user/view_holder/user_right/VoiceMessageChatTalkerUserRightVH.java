package apptribus.com.tribus.activities.chat_user.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;
import static apptribus.com.tribus.util.Constantes.VOICES_USERS;

/**
 * Created by User on 12/14/2017.
 */

public class VoiceMessageChatTalkerUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    //voice message
    @BindView(R.id.relative_voice)
    public RelativeLayout mRelativeVoice;

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

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private StorageReference mStorageVoiceUsers;
    private DatabaseReference mRefTalkersMessage;

    //SHOW
    private ProgressDialog progress;

    //--------------------------------------------- VARIABLES -------------------------------------------------------------
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

    public VoiceMessageChatTalkerUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageVoiceUsers = mStorage.getReference().child(VOICES_USERS);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

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


    public void initVoiceMessageChatTalkerUserRightVH(MessageUser message) {

        mBtnPlayAudioUserRight.setVisibility(VISIBLE);
        mBtnPauseAudioUserRight.setVisibility(GONE);
        mTvAudioStartUserRight.setVisibility(VISIBLE);
        mTvAudioEndUserRight.setVisibility(VISIBLE);
        mTvMessageTimeAudioUserRight.setVisibility(VISIBLE);
        mSeekBarVoiceMessageUserRight.setVisibility(VISIBLE);
        mCircleImageUserRightAudioMessage.setVisibility(VISIBLE);

        //SET MESSAGE TIME - AUDIO
        //setTvMessageTimeAudioUserRight(message.getTimestampCreatedLong());

        if (message.getAudio().getDuration() != null) {
            setAudioDurationUserRight(message.getAudio().getDuration());
        }
        downloadAudioUserRight(message);

    }

    private void downloadAudioUserRight(MessageUser message) {

        if (message.getAudio().getDownloadUri() != null) {

            Uri audioUri = Uri.parse(message.getAudio().getDownloadUri());

            initPlayerUserRight(0, audioUri);

            mBtnPlayAudioUserRight.setOnClickListener(v -> {

                initPlayButtonUserRight();

            });

            mBtnPauseAudioUserRight.setOnClickListener(v -> {
                initPauseButtonUserRight();
            });

        }
    }

    public void clickToDeleteVoiceMessageUserRight(MessageUser message, String mTalkerId) {
        mRelativeVoice.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(message, mTalkerId);
            }
        });

    }

    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(MessageUser message, String mTalkerId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage("Mensagem de aúdio");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteAudio(message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //SHOW PROGRESS
    private void showProgressDialog(boolean load, String message) {

        if (load) {
            progress.setMessage(message);
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private void deleteAudio(MessageUser message, String mTalkerId) {

        StorageReference fileRef = mStorage.getReferenceFromUrl(message.getAudio().getDownloadUri());

        fileRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            mRefTalkersMessage
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTalkerId)
                    .child(message.getKey())
                    .removeValue()
                    .addOnSuccessListener(aVoid2 -> {
                        showProgressDialog(false, null);
                        Toast.makeText(mContext, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }).addOnFailureListener(e -> {
            e.getLocalizedMessage();
            showProgressDialog(false, null);
            Toast.makeText(mContext, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();

        });

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

}
