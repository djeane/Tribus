package apptribus.com.tribus.activities.chat_tribu.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
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
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.FROM_CHAT_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_PERMISSIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS_ADDED;
import static apptribus.com.tribus.util.Constantes.USER_ID;

/**
 * Created by User on 12/13/2017.
 */

public class VoiceMessageChatTribuUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.row_voice_message_user_right_tribu)
    RelativeLayout mlayoutRowVoiceMessageUserRight;

    @BindView(R.id.relative_voice)
    RelativeLayout mRelativeVoice;

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
    private DatabaseReference mRefTribusMessage;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalkersInvitations;
    private DatabaseReference mRefUsersTalk;
    private DatabaseReference mReferenceTribu;
    private DatabaseReference mRefUsersTalkAdded;
    private DatabaseReference mRefUsersTalkersPermissions;

    //SHOW PROGRESS
    private ProgressDialog progress;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mUsersCollections;


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


    public VoiceMessageChatTribuUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
        mRefUsersTalkAdded = mDatabase.getReference().child(USERS_TALKS_ADDED);
        mRefUsersTalkersPermissions = mDatabase.getReference().child(USERS_TALKERS_PERMISSIONS);
        mRefUsersTalkersInvitations = mDatabase.getReference().child(USERS_TALKERS_INVITATIONS);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollections = mFirestore.collection(GENERAL_USERS);



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
    private void setTvMessageTimeAudioUserRight(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeAudioUserRight.setText(time);
    }

    private void setAudioDurationUserRight(String duration){
        mTvAudioEndUserRight.setText(duration);
    }


    private void setUserNameUserRightVoice(String username, String name) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";
        mTvUserNameVoice.setText(appendNameAndUsername);
    }

    //set image
    private void setImageUserRightAudioMessage(String url) {

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);

                //Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                //Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                //Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                //Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                //Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
            }
        };

        //SCRIPT - LARGURA DA IMAGEM
        //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

        Uri uri = Uri.parse(url);
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(listener)
                .setOldController(mCircleImageUserRightAudioMessage.getController())
                .build();
        mCircleImageUserRightAudioMessage.setController(dc);

    }


    public void initVoiceMessageChatTribuUserRightVH(MessageUser message,
                                                     ChatTribuView mView, String mTribusKey, Boolean mIsPublic) {

        mCircleImageUserRightAudioMessage.bringToFront();
        mRelativeVoice.invalidate();

        mUsersCollections
                .document(message.getUidUser())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){

                        User mUserRight = documentSnapshot.toObject(User.class);

                        if (message.getAudio().getDownloadUri() != null) {
                            //viewHolder.mIncludeLayoutAudioMessageUserRight.setVisibility(VISIBLE);
                            mBtnPlayAudioUserRight.setVisibility(VISIBLE);
                            mBtnPauseAudioUserRight.setVisibility(GONE);
                            mTvAudioStartUserRight.setVisibility(VISIBLE);
                            mTvAudioEndUserRight.setVisibility(VISIBLE);
                            mTvMessageTimeAudioUserRight.setVisibility(VISIBLE);
                            mSeekBarVoiceMessageUserRight.setVisibility(VISIBLE);
                            mCircleImageUserRightAudioMessage.setVisibility(VISIBLE);

                            setUserNameUserRightVoice(mUserRight.getUsername(), mUserRight.getName());

                            //SET IMAGE
                            if (mUserRight.getThumb() != null) {
                                setImageUserRightAudioMessage(mUserRight.getThumb());
                            }
                            else {
                                setImageUserRightAudioMessage(mUserRight.getImageUrl());
                            }


                            //SET MESSAGE TIME - AUDIO
                            setTvMessageTimeAudioUserRight(message.getDate());

                            if (message.getAudio().getDuration() != null) {
                                setAudioDurationUserRight(message.getAudio().getDuration());
                            }

                            downloadAudioUserRight(message);

                            mCircleImageUserRightAudioMessage.setOnClickListener(v -> {

                                openDetailActivity(mView, mUserRight.getId(), mAuth.getCurrentUser().getUid(), mTribusKey);

                            });

                        }

                    }
                    mlayoutRowVoiceMessageUserRight.setVisibility(VISIBLE);

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(mView.mContext.getApplicationContext(), "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                });

    }

    private void openDetailActivity(ChatTribuView view, String talkerId, String userId, String mTribusKey){

        Intent intent = new Intent(view.mContext, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra(FROM_CHAT_TRIBUS, FROM_CHAT_TRIBUS);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(USER_ID, userId);
        view.mContext.startActivity(intent);
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


    private void openChatTalker(String talkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra("fromChatTribu", "fromChatTribu");
        mContext.startActivity(intent);
    }

    private void showToastIfWaitingPermission(String userRightId) {

        mReferenceUser
                .child(userRightId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        String message = user.getName() + " ainda não aceitou seu convite para " +
                                "conversar de modo restrito.";

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });


    }



    //ADD USER AS TALKER
    //SHOW DIALOG
    private void showDialog(Context context, MessageUser message, String mTribusKey) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(context);
        progress.setCancelable(false);

        mReferenceUser
                .child(message.getUidUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User userRight = dataSnapshot.getValue(User.class);

                        //CONFIGURATION OF DIALOG
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Adicionar contato");
                        builder.setMessage("Conversar com " + userRight.getName() +
                                "(" + userRight.getUsername() + ")?");

                        String positiveText = "SIM";
                        builder.setPositiveButton(positiveText, (dialog, which) -> {
                            dialog.dismiss();
                            showProgressDialog(true, "Aguarde enquanto o contato é adicionado...");
                            createTalk(context, userRight, mTribusKey);

                        });

                        String negativeText = "NÃO";
                        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
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

    //CREATE TALKS
    private void createTalk(Context context, User userInvited, String mTribusKey) {

        /*mReferenceTribu
                .child(mTribusKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Tribu tribu = dataSnapshot.getValue(Tribu.class);

                        Date date = new Date(System.currentTimeMillis());

                        //USER RIGHT
                        Talk talkerUserRight = new Talk(tribu.getProfile().getNameTribu(), tribu.getProfile().getUniqueName(),
                                userInvited.getId());
                        talkerUserRight.setDateInvitation(date);

                        //USER LEFT
                        Talk talkerUserLeft = new Talk(tribu.getProfile().getNameTribu(), tribu.getProfile().getUniqueName(),
                                mAuth.getCurrentUser().getUid());
                        talkerUserLeft.setDateInvitation(date);

                        if (userInvited.isAccepted()) {
                            talkerUserRight.setFromPermission(false);
                            talkerUserLeft.setFromPermission(false);
                            //STORE TALK RIGHT INSIDE TALK LEFT
                            mRefUsersTalk
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(userInvited.getId())
                                    .setValue(talkerUserRight)
                                    .addOnSuccessListener(task -> {

                                        //STORE TALK LEFT INSIDE TALK RIGHT
                                        mRefUsersTalk
                                                .child(userInvited.getId())
                                                .child(mAuth.getCurrentUser().getUid())
                                                .setValue(talkerUserLeft)
                                                .addOnSuccessListener(task1 -> {

                                                    //ADD TALKER ADDED INSIDE THIS NODE(TALKER ADDED)
                                                    mRefUsersTalkAdded
                                                            .child(mAuth.getCurrentUser().getUid())
                                                            .child(userInvited.getId())
                                                            .setValue(talkerUserLeft)
                                                            .addOnSuccessListener(task2 -> {
                                                                //SHOW
                                                                showProgressDialog(false, null);
                                                                Toast.makeText(context, "Contato adicionado!", Toast.LENGTH_SHORT).show();
                                                                mCircleImageUserRightAudioMessage.setEnabled(false);
                                                            })
                                                            .addOnFailureListener(Throwable::getLocalizedMessage);

                                                })
                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                    })
                                    .addOnFailureListener(Throwable::getLocalizedMessage);

                        } else {

                            //TALKER RIGHT - TO SEE HIS TALKS WHICH ARE WAITING PERMISSION
                            mRefUsersTalkersPermissions
                                    .child(userInvited.getId())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(talkerUserLeft)
                                    .addOnSuccessListener(taskUserLeft -> {

                                        //USER LEFT - TO SEE HIS INVITATIONS
                                        mRefUsersTalkersInvitations
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child(userInvited.getId())
                                                .setValue(talkerUserRight)
                                                .addOnSuccessListener(taskUserRight -> {

                                                    //SHOW
                                                    showProgressDialog(false, null);
                                                    Toast.makeText(context, "Convite enviado!", Toast.LENGTH_SHORT).show();
                                                    mCircleImageUserRightAudioMessage.setEnabled(false);
                                                })
                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                    })
                                    .addOnFailureListener(Throwable::getLocalizedMessage);


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }
                });*/


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
