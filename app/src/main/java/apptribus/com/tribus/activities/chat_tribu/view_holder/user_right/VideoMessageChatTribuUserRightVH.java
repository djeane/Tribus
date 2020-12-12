package apptribus.com.tribus.activities.chat_tribu.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
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
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.show_video.ShowVideoActivity;
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
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
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

public class VideoMessageChatTribuUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.relative_video)
    RelativeLayout mRelativeVideo;

    @BindView(R.id.layout_video_user_right)
    RelativeLayout mLayoutVideoUserRight;

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

    @BindView(R.id.linear_video_info_user_right)
    public LinearLayout mLinearVideoInformationUserRight;

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
    public SimpleExoPlayer mExoplayer;

    public VideoMessageChatTribuUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollections = mFirestore.collection(GENERAL_USERS);


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

    }

    // VIDEO MESSAGE
    //set image
    private void setImageUserRightVideoMessage(String url) {

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
                .setOldController(mCircleImageUserRightVideoMessage.getController())
                .build();
        mCircleImageUserRightVideoMessage.setController(dc);

    }

    private void setUserNameUserRightVideo(String username, String name) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";
        mTvUserNameVideo.setText(appendNameAndUsername);
    }


    private void setTvDescriptionRight(String description) {
        mTvVideoDescriptionUserRight.setText(description);
    }

    private void setTvMessageTimeVideoUserRight(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeVideoUserRight.setText(time);
    }

    public void initVideoMessageChatTribuUserRightVH(MessageUser message,
                                                     ChatTribuView mView, String mTribusKey, Boolean mIsPublic) {

        mCircleImageUserRightVideoMessage.bringToFront();
        mRelativeVideo.invalidate();

        mUsersCollections
                .document(message.getUidUser())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){

                        User mUserRight = documentSnapshot.toObject(User.class);

                        if (message.getVideo().getDownloadUri() != null) {

                            mBtnDownloadVideoUserRight.setVisibility(GONE);

                            //SETUPS
                            setUserNameUserRightVideo(mUserRight.getUsername(), mUserRight.getName());

                            if (mUserRight.getThumb() != null) {
                                setImageUserRightVideoMessage(mUserRight.getThumb());
                            } else {
                                setImageUserRightVideoMessage(mUserRight.getImageUrl());
                            }

                            mRelativeLoadingPanelUserRight.setVisibility(VISIBLE);
                            mProgressVideoUserRight.setVisibility(GONE);
                            //viewHolder.setTvSizeRight(String.valueOf(message.getVideo().getSize()));

                            setTvMessageTimeVideoUserRight(message.getDate());

                            if (message.getVideo().getDescription() != null) {
                                mTvVideoDescriptionUserRight.setVisibility(VISIBLE);
                                setTvDescriptionRight(message.getVideo().getDescription());
                            } else {
                                mTvVideoDescriptionUserRight.setVisibility(GONE);

                            }


                            Uri videoUri = Uri.parse(message.getVideo().getDownloadUri());
                            initPlayerUserRight(videoUri);

                            mRelativeLoadingPanelUserRight.setOnClickListener(v -> {
                                openShowVideoActivity(videoUri, message.getKey(), mTribusKey);
                            });

                            mBtnPlayVideoUserRight.setOnClickListener(v -> {
                                openShowVideoActivity(videoUri, message.getKey(), mTribusKey);

                            });

                            mCircleImageUserRightVideoMessage.setOnClickListener(v -> {

                                openDetailActivity(mView, mUserRight.getId(), mAuth.getCurrentUser().getUid(), mTribusKey);

                            });

                        }
                    }
                    mLayoutVideoUserRight.setVisibility(VISIBLE);

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(mView.mContext.getApplicationContext(), "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                });
    }

    private void openShowVideoActivity(Uri uri, String ref, String mTribusKey) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowVideoActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }

    private void openDetailActivity(ChatTribuView view, String talkerId, String userId, String mTribusKey){

        Intent intent = new Intent(view.mContext, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra(FROM_CHAT_TRIBUS, FROM_CHAT_TRIBUS);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(USER_ID, userId);
        view.mContext.startActivity(intent);
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
                                                                mCircleImageUserRightVideoMessage.setEnabled(false);
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
                                                    mCircleImageUserRightVideoMessage.setEnabled(false);
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

    //VIDEO
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



    private void openChatTalker(String talkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra("fromChatTribu", "fromChatTribu");
        mContext.startActivity(intent);
    }


}
