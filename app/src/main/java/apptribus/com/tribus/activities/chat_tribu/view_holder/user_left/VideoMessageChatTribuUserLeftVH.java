package apptribus.com.tribus.activities.chat_tribu.view_holder.user_left;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.show_video.ShowVideoActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.REMOVED;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_VIDEOS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/13/2017.
 */

public class VideoMessageChatTribuUserLeftVH extends RecyclerView.ViewHolder {

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
    private StorageReference mStorageTribusVideoMessages;
    private DatabaseReference mRefTribusMessage;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mTribusCollection;

    //SHOW PROGRESS
    private ProgressDialog progress;


    //--------------------------------------------- VARIABLES -------------------------------------------------------------
    //USER LEFT
    public SimpleExoPlayer mExoplayer;

    public VideoMessageChatTribuUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageTribusVideoMessages = mStorage.getReference().child(TRIBUS_VIDEOS_MESSAGES);
        mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);


    }

    //VIDEO MESSAGE
    public void setTvDurationLeft(String duration) {
        mTvVideoDurationUserLeft.setText(duration);
    }

    public void setTvDescriptionLeft(String description) {
        mTvVideoDescriptionUserLeft.setText(description);
    }

    public void setTvMessageTimeVideoUserLeft(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeVideoUserLeft.setText(time);
    }

    public void initVideoMessageChatTribuUserLeftVH(MessageUser message, ChatTribuView mView, String mTribusKey,
                                                    Uri mVideoUri, String mVideoPath, Boolean mIsPublic) {

        mIvGarbageMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                showDialogToDeleteMessage(mContext, message, mView, mTribusKey);
            }
        });

        //viewHolder.mIncludeLayoutVideoMessageUserLeft.setVisibility(VISIBLE);
        mRelativeLoadingPanelUserLeft.setVisibility(VISIBLE);

        //SETUPS
        setTvDurationLeft(message.getVideo().getDuration());

        if (message.getVideo().getDescription() != null) {
            mTvVideoDescriptionUserLeft.setVisibility(VISIBLE);
            setTvDescriptionLeft(message.getVideo().getDescription());
        } else {
            mTvVideoDescriptionUserLeft.setVisibility(GONE);

        }

        setTvMessageTimeVideoUserLeft(message.getDate());

        mBtnDownloadVideoUserLeft.setVisibility(GONE);
        if (message.getVideo().isUploaded()) {
            downloadVideo(message, mTribusKey);


        } else {
            mProgressVideoUserLeft.setVisibility(VISIBLE);
            uploadVideoToFirebase(mVideoUri, mVideoPath, message.getKey(), message, mTribusKey);
        }

        mLayoutVideoUserLeft.setVisibility(VISIBLE);
    }

    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(Context context, MessageUser message, ChatTribuView mView, String mTribusKey) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage("Mensagem de vídeo");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida... ");
                deleteVideo(context, message, mTribusKey);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteVideo(Context context, MessageUser message, String mTribusKey) {

        StorageReference fileRef = mStorage.getReferenceFromUrl(message.getVideo().getDownloadUri());
        fileRef.delete().addOnSuccessListener(aVoid -> {

            Map<String, Object> updateTypeMessageToRemoved = new HashMap<>();
            updateTypeMessageToRemoved.put("contentType", REMOVED);

            mTribusCollection
                    .document(mTribusKey)
                    .collection(TOPICS)
                    .document(message.getTopicKey())
                    .collection(TOPIC_MESSAGES)
                    .document(message.getKey())
                    .update(updateTypeMessageToRemoved)
                    .addOnSuccessListener(aVoid2 -> {
                        showProgressDialog(false, null);
                        /*Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                                Toast.LENGTH_LONG).show();*/
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

            // File deleted successfully
            /*mRefTribusMessage
                    .child(mTribusUniqueName)
                    .child(message.getTopicKey())
                    .child(message.getKey())
                    .removeValue()
                    .addOnSuccessListener(aVoid2 -> {
                        showProgressDialog(false, null);
                        Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                                Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);*/

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



    //UPLOAD VIDEOS TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadVideoToFirebase(Uri mVideoUri, String mVideoPath, String messageRef, MessageUser message, String mTribusKey) {

        DatabaseReference refMessage = mRefTribusMessage
                .child(mTribusKey)
                .child(message.getTopicKey())
                .child(messageRef);

        final StorageReference filePath = mStorageTribusVideoMessages
                .child(mAuth.getCurrentUser().getUid())
                .child("video")
                .child(mVideoPath + "/video_message_left.mp4");

        filePath.putFile(mVideoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    String uri = filePath.getDownloadUrl().toString();
                    Map<String, Object> updateMessage = new HashMap<>();
                    updateMessage.put("uploaded", true);
                    updateMessage.put("downloadUri", uri);

                    refMessage.child("video")
                            .updateChildren(updateMessage)
                            .addOnSuccessListener(aVoid -> {
                                downloadVideo(message, mTribusKey);
                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);


                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(mContext.getApplicationContext(), "Erro ao enviar video para Firebase: put file", Toast.LENGTH_LONG).show();
                    refMessage.removeValue();

                })
                .addOnProgressListener(taskSnapshot -> {
                    //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();;
                    //System.out.println("Upload is " + progress + "% done");
                    int currentprogress = (int) progress;
                    //progressBar.setProgress(currentprogress);
                    mProgressVideoUserLeft.setProgress(currentprogress);


                });

    }


    private void downloadVideo(MessageUser message, String mTribusKey) {

        if (message.getVideo().getDownloadUri() != null) {

            Uri videoUri = Uri.parse(message.getVideo().getDownloadUri());
            initPlayer(videoUri);


            mRelativeLoadingPanelUserLeft.setOnClickListener(v -> {

                openShowVideoActivity(videoUri, message.getKey(), mTribusKey);

            });

            mBtnPlayVideoUserLeft.setOnClickListener(v -> {
                openShowVideoActivity(videoUri, message.getKey(), mTribusKey);
            });

        }
    }

    //INIT VIDEO
    private void initPlayer(Uri videoUri) {
        mSimplePlayerUserLeft.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

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

    private void openShowVideoActivity(Uri uri, String ref, String mTribusKey) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowVideoActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }


}