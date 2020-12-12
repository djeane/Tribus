package apptribus.com.tribus.activities.chat_user.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_video_talker.ShowVideoTalkerActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;
import static apptribus.com.tribus.util.Constantes.VIDEOS_USERS;

/**
 * Created by User on 12/14/2017.
 */

public class VideoMessageChatTalkerUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    //video messages
    @BindView(R.id.layout_video_user_right)
    public RelativeLayout mRelativeVideo;

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


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private StorageReference mStorageVideosUsers;
    private DatabaseReference mRefTalkersMessage;

    //SHOW
    private ProgressDialog progress;


    //--------------------------------------------- VARIABLES -------------------------------------------------------------
    //USER RIGHT
    public SimpleExoPlayer mExoplayer;

    public VideoMessageChatTalkerUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageVideosUsers = mStorage.getReference().child(VIDEOS_USERS);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

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


    public void initVideoMessageChatTalkerUserRightVH(MessageUser message, String mTalkerId) {

        //SETUP INCLUDE VISIBILITIES
        mBtnDownloadVideoUserRight.setVisibility(GONE);
        mRelativeLoadingPanelUserRight.setVisibility(VISIBLE);
        mProgressVideoUserRight.setVisibility(GONE);
        mTvVideoSizeUserRight.setVisibility(GONE);
        setTvDurationRight(message.getVideo().getDuration());
        //viewHolder.setTvSizeRight(String.valueOf(message.getVideo().getSize()));
        //setTvMessageTimeVideoUserRight(message.getTimestampCreatedLong());
        if (message.getVideo().getDescription() != null) {
            mTvVideoDescriptionUserRight.setVisibility(VISIBLE);
            setTvDescriptionRight(message.getVideo().getDescription());
        } else {
            mTvVideoDescriptionUserRight.setVisibility(GONE);
        }

        if (message.getVideo().getDownloadUri() != null) {

            Uri videUri = Uri.parse(message.getVideo().getDownloadUri());
            initPlayerUserRight(videUri);

            mRelativeLoadingPanelUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowVideoTalkerActivity(videUri, message.getKey(), mTalkerId);
                }
            });

            mBtnPlayVideoUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowVideoTalkerActivity(videUri, message.getKey(), mTalkerId);
                }
            });

        }
    }

    public void clickToDeleteVideoMessageUserRight(MessageUser message, String mTalkerId) {
        mRelativeVideo.setOnClickListener(v -> {
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

    //USER RIGHT//SHOW PROGRESS
    private void showProgressDialog(boolean load, String message) {

        if (load) {
            progress.setMessage(message);
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private void openShowVideoTalkerActivity(Uri uri, String ref, String mTalkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowVideoTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }



    private void initPlayerUserRight(Uri videoUri) {
        mSimplePlayerUserRight.requestFocus();

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
        mSimplePlayerUserRight.setPlayer(mExoplayer);
        mExoplayer.setPlayWhenReady(false);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        mExoplayer.prepare(mediaSource);

        mBtnPlayVideoUserRight.setVisibility(VISIBLE);
        mProgressVideoUserRight.setVisibility(GONE);

    }

}
