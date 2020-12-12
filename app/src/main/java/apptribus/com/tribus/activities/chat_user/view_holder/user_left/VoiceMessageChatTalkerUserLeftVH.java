package apptribus.com.tribus.activities.chat_user.view_holder.user_left;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_AUDIO_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.VOICES_USERS;

/**
 * Created by User on 12/14/2017.
 */

public class VoiceMessageChatTalkerUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.layout_voice_user_left)
    RelativeLayout mLayoutVoiceUserLeft;

    @BindView(R.id.relative_voice)
    public RelativeLayout mRelativeVoice;

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

    @BindView(R.id.iv_garbage_message)
    ImageView mIvGarbageMessage;


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefTalkersMessage;
    private FirebaseStorage mStorage;
    private StorageReference mStorageVoiceUsers;



    //SHOW PROGRESS
    private ProgressDialog progress;


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
    public Handler mHandlerFinished;
    public long playbackPosition;
    public int currentWindow;
    public boolean playWhenReady;

    public VoiceMessageChatTalkerUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageVoiceUsers = mStorage.getReference().child(VOICES_USERS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);


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

        mLayoutVoiceUserLeft.setVisibility(VISIBLE);
    }

    // VOICE MESSAGE
    //set time voice message
    public void setTvMessageTimeAudioUserLeft(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeAudioUserLeft.setText(time);
    }

    public void setAudioDurationUserLeft(String duration){
        mTvAudioEndUserLeft.setText(duration);
    }


    public void initVoiceMessageChatTalkerUserLeftVH(MessageUser message, String mTalkerID) {
        if (message.getAudio().isUploaded()) {
            downloadAudio(message);
        } else {

            uploadAudiToFirebase2(message.getKey(), message, mTalkerID);
        }
    }

    public void clickToDeleteVoiceMessageUserLeft(MessageUser message, String mTalkerId) {
        //LISTENERS TO DELETE MESSAGES
        mIvGarbageMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message, mTalkerId);
            }
        });

    }

    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(Context context, MessageUser message, String mTalkerId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage("Mensagem de aúdio");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteAudio(context, message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAudio(Context context, MessageUser message, String mTalkerId) {

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
                        Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }).addOnFailureListener(e -> {
            e.getLocalizedMessage();
            showProgressDialog(false, null);
            Toast.makeText(context, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();

        });

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



    @SuppressWarnings("VisibleForTests")
    private void uploadAudiToFirebase2(String messageRef, MessageUser message, String mTalkerId) {

        //VIDEO MESSAGE INSIDE CURRENT TALKER
        DatabaseReference refMessage = mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId)
                .child(messageRef);


        //VIDEO MESSAGE INSIDE RIGHT TALKER
        DatabaseReference refMessageRightTalker = mRefTalkersMessage
                .child(mTalkerId)
                .child(mAuth.getCurrentUser().getUid())
                .child(messageRef);

        mProgressLoadingAudio.setVisibility(VISIBLE);
        mBtnPlayAudioUserLeft.setVisibility(GONE);
        mBtnPauseAudioUserLeft.setVisibility(GONE);
        mTvAudioStartUserLeft.setVisibility(GONE);
        mTvAudioEndUserLeft.setVisibility(GONE);
        mTvMessageTimeAudioUserLeft.setVisibility(GONE);
        mSeekBarVoiceMessageUserLeft.setVisibility(GONE);


        StorageReference storageRef = mStorage.getReference().child(message.getMessage());
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Map<String, Object> updateMessage = new HashMap<>();
            updateMessage.put("uploaded", true);
            updateMessage.put("downloadUri", uri);

            refMessage
                    .child("audio")
                    .updateChildren(updateMessage)
                    .addOnSuccessListener(aVoid -> {

                        refMessageRightTalker
                                .child("audio")
                                .updateChildren(updateMessage)
                                .addOnSuccessListener(aVoid1 -> {
                                    downloadAudio(message);

                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);

                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }).addOnFailureListener(Throwable::getLocalizedMessage);

    }

    //COMMON CODE
    private void downloadAudio(MessageUser message) {

        if (message.getAudio().getDownloadUri() != null) {

            mProgressLoadingAudio.setVisibility(GONE);
            mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
            mBtnPauseAudioUserLeft.setVisibility(GONE);
            mTvAudioStartUserLeft.setVisibility(VISIBLE);
            mTvAudioEndUserLeft.setVisibility(VISIBLE);
            mTvMessageTimeAudioUserLeft.setVisibility(VISIBLE);
            mSeekBarVoiceMessageUserLeft.setVisibility(VISIBLE);

            //SET MESSAGE TIME - AUDIO
            //setTvMessageTimeAudioUserLeft(message.getTimestampCreatedLong());

            if(message.getAudio().getDuration() != null){
                setAudioDurationUserLeft(message.getAudio().getDuration());
            }

            initPlayer(0, Uri.parse(message.getAudio().getDownloadUri()));

            mBtnPlayAudioUserLeft.setOnClickListener(v -> {

                initPlayButton();

            });

            mBtnPauseAudioUserLeft.setOnClickListener(v -> {
                initPauseButton();
            });


        }

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

}
