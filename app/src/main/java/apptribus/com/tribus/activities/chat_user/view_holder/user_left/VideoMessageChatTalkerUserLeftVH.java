package apptribus.com.tribus.activities.chat_user.view_holder.user_left;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_video_talker.ShowVideoTalkerActivity;
import apptribus.com.tribus.pojo.ChatTalker;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.VIDEOS_USERS;

/**
 * Created by User on 12/14/2017.
 */

public class VideoMessageChatTalkerUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.layout_video_user_left)
    RelativeLayout mLayoutVideoUserLeft;

    @BindView(R.id.relative_video)
    public RelativeLayout mRelativeVideo;

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

    @BindView(R.id.iv_garbage_message)
    ImageView mIvGarbageMessage;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageVideosUsers;
    private DatabaseReference mRefTalkersMessage;
    private DatabaseReference mRefChatTalker;
    private DatabaseReference mReferenceUser;

    //SHOW PROGRESS
    private ProgressDialog progress;

    //--------------------------------------------- VARIABLES -------------------------------------------------------------
    //USER LEFT
    public SimpleExoPlayer mExoplayer;


    public VideoMessageChatTalkerUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageVideosUsers = mStorage.getReference().child(VIDEOS_USERS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        mLayoutVideoUserLeft.setVisibility(VISIBLE);
    }

    //VIDEO MESSAGE
    public void setTvDurationLeft(String duration) {
        mTvVideoDurationUserLeft.setText(duration);
    }

    public void setTvDescriptionLeft(String description) {
        mTvVideoDescriptionUserLeft.setText(description);
    }

    public void setTvMessageTimeVideoUserLeft(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeVideoUserLeft.setText(time);
    }

    public void initVideoMessageChatTalkerUserLeftVH(MessageUser message, String mTalkerId, String mVideoPath, Uri mVideoUri) {

        mRelativeLoadingPanelUserLeft.setVisibility(VISIBLE);

        //SETUPS
        setTvDurationLeft(message.getVideo().getDuration());

        if (message.getVideo().getDescription() != null) {
            mTvVideoDescriptionUserLeft.setVisibility(VISIBLE);
            setTvDescriptionLeft(message.getVideo().getDescription());
        } else {
            mTvVideoDescriptionUserLeft.setVisibility(GONE);
        }
        //setTvMessageTimeVideoUserLeft(message.getTimestampCreatedLong());

        mBtnDownloadVideoUserLeft.setVisibility(GONE);
        if (message.getVideo().isUploaded()) {

            downloadVideo(message, mTalkerId);

        } else {
            mProgressVideoUserLeft.setVisibility(VISIBLE);
            uploadVideoToFirebase(mVideoPath, mVideoUri, message.getKey(), message, mTalkerId);
        }
    }

    public void clickToDeleteVideoMessageUserLeft(MessageUser message, String mTalkerId) {

        mIvGarbageMessage.setOnClickListener(v -> {
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
        builder.setMessage("Mensagem de vídeo");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteVideo(message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteVideo(MessageUser message, String mTalkerId) {

        StorageReference fileRef = mStorage.getReferenceFromUrl(message.getVideo().getDownloadUri());

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


    //SHOW PROGRESS
    private void showProgressDialog(boolean load, String message) {

        if (load) {
            progress.setMessage(message);
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private void downloadVideo(MessageUser message, String mTalkerId) {

        if (message.getVideo().getDownloadUri() != null) {

            Uri videoUri = Uri.parse(message.getVideo().getDownloadUri());
            initPlayer(videoUri);
            mRelativeLoadingPanelUserLeft.setOnClickListener(v -> {
                openShowVideoTalkerActivity(videoUri, message.getKey(), mTalkerId);
            });

            mBtnPlayVideoUserLeft.setOnClickListener(v -> {
                openShowVideoTalkerActivity(videoUri, message.getKey(), mTalkerId);
            });

        }
    }

    private void openShowVideoTalkerActivity(Uri uri, String ref, String mTalkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowVideoTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }


    //INIT VIDEO
    private void initPlayer(Uri videoUri) {
        mSimplePlayerUserLeft.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoplayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        mSimplePlayerUserLeft.setPlayer(mExoplayer);
        mExoplayer.setPlayWhenReady(false);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        mExoplayer.prepare(mediaSource);
        mBtnPlayVideoUserLeft.setVisibility(VISIBLE);
        mProgressVideoUserLeft.setVisibility(GONE);

    }


    //UPLOAD VIDEOS TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadVideoToFirebase(String mVideoPath, Uri mVideoUri, String messageRef, MessageUser message, String mTalkerId) {

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

        final StorageReference filePath = mStorageVideosUsers
                .child(mAuth.getCurrentUser().getUid())
                .child("video")
                .child(mVideoPath + "/video_message_left.mp4");

        filePath.putFile(mVideoUri)
                .addOnSuccessListener(taskSnapshot -> {

                    String uri = filePath.getDownloadUrl().toString();
                    Date date = new Date(System.currentTimeMillis());
                    Map<String, Object> updateMessage = new HashMap<>();
                    updateMessage.put("uploaded", true);
                    updateMessage.put("downloadUri", uri);

                    //UPDATE VIDEO MESSAGE INSIDE CURRENT TALKER
                    refMessage
                            .child("video")
                            .updateChildren(updateMessage)
                            .addOnSuccessListener(aVoid -> {

                                //UPDATE VIDEO MESSAGE INSIDE RIGHT TALKER
                                refMessageRightTalker
                                        .child("video")
                                        .updateChildren(updateMessage)
                                        .addOnSuccessListener(aVoid1 -> {
                                            downloadVideo(message, mTalkerId);

                                            mReferenceUser
                                                    .child(mTalkerId)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            User talkerUser = dataSnapshot.getValue(User.class);

                                                            if (!talkerUser.isOnlineInChat()) {
                                                                updateNumUnreadMessages(mTalkerId);

                                                                updateChatTalker("Mensagem de Vídeo", mTalkerId);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            databaseError.toException().printStackTrace();
                                                        }
                                                    });
                                        })
                                        .addOnFailureListener(Throwable::getLocalizedMessage);

                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);


                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(mContext.getApplicationContext(), "Erro ao enviar video.", Toast.LENGTH_LONG).show();
                    refMessage.removeValue();

                })
                .addOnProgressListener(taskSnapshot -> {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();;
                    //System.out.println("Envio " + progress + "% concluído");
                    int currentprogress = (int) progress;
                    //progressBar.setProgress(currentprogress);
                    mProgressVideoUserLeft.setProgress(currentprogress);


                    //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                });

    }

    private void updateChatTalker(String message, String mTalkerId) {
        mRefChatTalker
                .child(mTalkerId)
                .child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ChatTalker chatTalkerSnapshot = dataSnapshot.getValue(ChatTalker.class);

                        Date date = new Date(System.currentTimeMillis());

                        Map<String, Object> updateChatTalker = new HashMap<>();
                        updateChatTalker.put("currentUserId", mAuth.getCurrentUser().getUid());
                        updateChatTalker.put("talkerId", mTalkerId);
                        updateChatTalker.put("talkerIsOnline", false);
                        updateChatTalker.put("date", date);
                        updateChatTalker.put("message", message);

                        if (!dataSnapshot.hasChildren()) {
                            updateChatTalker.put("unreadMessages", 1);
                        } else {
                            if (chatTalkerSnapshot.getUnreadMessages() == 0) {
                                updateChatTalker.put("unreadMessages", 1);
                            } else {
                                updateChatTalker.put("unreadMessages",
                                        chatTalkerSnapshot.getUnreadMessages() + 1);
                            }
                        }

                        mRefChatTalker
                                .child(mTalkerId)
                                .child(mAuth.getCurrentUser().getUid())
                                .updateChildren(updateChatTalker)
                                .addOnFailureListener(Throwable::getLocalizedMessage);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }


    private void updateNumUnreadMessages(String mTalkerId) {
        mRefChatTalker
                .child(mTalkerId)
                .child(mAuth.getCurrentUser().getUid())
                //.child("unreadMessages")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        ChatTalker chatTalker = (ChatTalker) mutableData.getValue();
                        Date date = new Date(System.currentTimeMillis());

                        if (chatTalker != null) {
                            chatTalker.setUnreadMessages(chatTalker.getUnreadMessages() + 1);
                            chatTalker.setDate(date);
                        } else {

                            chatTalker = new ChatTalker();

                            chatTalker.setCurrentUserId(mAuth.getCurrentUser().getUid());
                            chatTalker.setTalkerId(mTalkerId);
                            chatTalker.setDate(date);
                            chatTalker.setTalkerIsOnline(false);
                            chatTalker.setUnreadMessages(1);
                        }

                        mutableData.setValue(chatTalker);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }

}
