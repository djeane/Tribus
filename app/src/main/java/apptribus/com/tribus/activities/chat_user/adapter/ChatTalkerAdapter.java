package apptribus.com.tribus.activities.chat_user.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_user.view_holder.user_left.ImageMessageChatTalkerUserLeftVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_left.TextMessageChatTalkerUserLeftVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_left.VideoMessageChatTalkerUserLeftVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_left.VoiceMessageChatTalkerUserLeftVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_right.ImageMessageChatTalkerUserRightVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_right.TextMessageChatTalkerUserRightVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_right.VideoMessageChatTalkerUserRightVH;
import apptribus.com.tribus.activities.chat_user.view_holder.user_right.VoiceMessageChatTalkerUserRightVH;
import apptribus.com.tribus.activities.show_image_talker.ShowImageTalkerActivity;
import apptribus.com.tribus.activities.show_video_talker.ShowVideoTalkerActivity;
import apptribus.com.tribus.pojo.ChatTalker;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.IMAGE;
import static apptribus.com.tribus.util.Constantes.IMAGE_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.IMAGE_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.LINK;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.TALKERS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TEXT_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.TEXT_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_PERMISSIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;
import static apptribus.com.tribus.util.Constantes.VIDEO;
import static apptribus.com.tribus.util.Constantes.VIDEOS_USERS;
import static apptribus.com.tribus.util.Constantes.VIDEO_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.VIDEO_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.VOICE;
import static apptribus.com.tribus.util.Constantes.VOICES_USERS;
import static apptribus.com.tribus.util.Constantes.VOICE_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.VOICE_MESSAGE_USER_RIGHT;

/**
 * Created by User on 9/14/2017.
 */

public class ChatTalkerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MessageUser> mMessageList;
    private MessageUser messageUser;
    private String mTalkerId;
    private Uri mVideoUri;
    private String mVideoPath;
    private Uri mImageUri;
    private String mImagePath;

    //SHOW
    private ProgressDialog progress;


    private File mAudioFolder;
    private File mVideoFolder;
    private File mImageFolder;
    private User mUserRight;
    private User mCurrentUser;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private StorageReference mStorageVoiceUsers;
    private StorageReference mStorageVideosUsers;
    private DatabaseReference mRefUsersTalkersPermissions;
    private DatabaseReference mRefUsersTalkersInvitations;
    private DatabaseReference mReferenceTribu;
    private DatabaseReference mRefTalkersMessage;
    private StorageReference mStorageImagesTalkers;
    private DatabaseReference mRefChatTalker;


    public ChatTalkerAdapter(Context context, List<MessageUser> mMessageList, String mTalkerId, Uri mVideoUri,
                             String mVideoPath, Uri mImageUri, String mImagePath) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageVoiceUsers = mStorage.getReference().child(VOICES_USERS);
        mStorageVideosUsers = mStorage.getReference().child(VIDEOS_USERS);
        mStorageImagesTalkers = mStorage.getReference().child(TALKERS_IMAGES_MESSAGES);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mRefUsersTalkersPermissions = mDatabase.getReference().child(USERS_TALKERS_PERMISSIONS);
        mRefUsersTalkersInvitations = mDatabase.getReference().child(USERS_TALKERS_INVITATIONS);
        mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);

        mContext = context;
        this.mMessageList = mMessageList;
        this.mTalkerId = mTalkerId;
        this.mVideoUri = mVideoUri;
        this.mVideoPath = mVideoPath;
        this.mImageUri = mImageUri;
        this.mImagePath = mImagePath;
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        setHasStableIds(true);
    }

    @Override
    public int getItemCount() {
        if (mMessageList != null) {
            return mMessageList.size();
        } else {
            return 0;
        }

    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    //NEW CODE - VIEW TYPES
    //VIEW TYPES
    @Override
    public int getItemViewType(int position) {
        if (mMessageList != null) {
            MessageUser message = mMessageList.get(position);
            if (message.getUidUser().equals(mAuth.getCurrentUser().getUid())) {
                switch (message.getContentType()) {
                    case TEXT:
                    case LINK:
                        return TEXT_MESSAGE_USER_LEFT;
                    case VOICE:
                        return VOICE_MESSAGE_USER_LEFT;
                    case IMAGE:
                        return IMAGE_MESSAGE_USER_LEFT;
                    case VIDEO:
                        return VIDEO_MESSAGE_USER_LEFT;
                    default:
                        return -1;
                }
            } else {
                switch (message.getContentType()) {
                    case TEXT:
                    case LINK:
                        return TEXT_MESSAGE_USER_RIGHT;
                    case VOICE:
                        return VOICE_MESSAGE_USER_RIGHT;
                    case IMAGE:
                        return IMAGE_MESSAGE_USER_RIGHT;
                    case VIDEO:
                        return VIDEO_MESSAGE_USER_RIGHT;
                    default:
                        return -1;
                }
            }
        }
        else {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TEXT_MESSAGE_USER_LEFT:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text_message_user_left, parent, false);
                return new TextMessageChatTalkerUserLeftVH(v);

            case VOICE_MESSAGE_USER_LEFT:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_message_user_left, parent, false);
                return new VoiceMessageChatTalkerUserLeftVH(v1);

            case VIDEO_MESSAGE_USER_LEFT:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_message_left, parent, false);
                return new VideoMessageChatTalkerUserLeftVH(v2);

            case IMAGE_MESSAGE_USER_LEFT:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_user_left, parent, false);
                return new ImageMessageChatTalkerUserLeftVH(v3);

            case TEXT_MESSAGE_USER_RIGHT:
                View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text_message_user_right, parent, false);
                return new TextMessageChatTalkerUserRightVH(v4);

            case VOICE_MESSAGE_USER_RIGHT:
                View v5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_message_user_right, parent, false);
                return new VoiceMessageChatTalkerUserRightVH(v5);

            case VIDEO_MESSAGE_USER_RIGHT:
                View v6 = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_message_right, parent, false);
                return new VideoMessageChatTalkerUserRightVH(v6);

            case IMAGE_MESSAGE_USER_RIGHT:
                View v7 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_user_right, parent, false);
                return new ImageMessageChatTalkerUserRightVH(v7);

            default:
                throw new RuntimeException("Não há layout para essa view.");
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        MessageUser message = mMessageList.get(position);

        if (message.getUidUser().equals(mAuth.getCurrentUser().getUid())) {
            switch (message.getContentType()) {
                case TEXT:
                    ((TextMessageChatTalkerUserLeftVH) viewHolder).initTextMessageChatTalkerUserLeftVH(message);
                    ((TextMessageChatTalkerUserLeftVH) viewHolder).clickToDeleteTextMessageUserLeft(message, mTalkerId);
                    break;

                case LINK:
                    ((TextMessageChatTalkerUserLeftVH) viewHolder).initLinkMessageChatTalkerUserLeftVH(message);
                    ((TextMessageChatTalkerUserLeftVH) viewHolder).clickToDeleteTextMessageUserLeft(message, mTalkerId);
                    break;

                case VOICE:
                    ((VoiceMessageChatTalkerUserLeftVH) viewHolder).initVoiceMessageChatTalkerUserLeftVH(message, mTalkerId);
                    ((VoiceMessageChatTalkerUserLeftVH) viewHolder).clickToDeleteVoiceMessageUserLeft(message, mTalkerId);
                    break;

                case VIDEO:
                    ((VideoMessageChatTalkerUserLeftVH) viewHolder).initVideoMessageChatTalkerUserLeftVH(message, mTalkerId, mVideoPath,
                            mVideoUri);
                    ((VideoMessageChatTalkerUserLeftVH) viewHolder).clickToDeleteVideoMessageUserLeft(message, mTalkerId);
                    break;

                case IMAGE:
                    ((ImageMessageChatTalkerUserLeftVH) viewHolder).initImageMessageChatTalkerUserLeftVH(message, mTalkerId, mImagePath,
                            mImageUri);
                    ((ImageMessageChatTalkerUserLeftVH) viewHolder).clickToDeleteImageMessageUserLeft(message, mTalkerId);
                    break;
            }
        }
        else {
            switch (message.getContentType()) {
                case TEXT:
                    ((TextMessageChatTalkerUserRightVH) viewHolder).initTextMessageChatTalkerUserRightVH(message);
                    ((TextMessageChatTalkerUserRightVH) viewHolder).clickToDeleteTextMessageUserRight(message, mTalkerId);
                    break;

                case LINK:
                    ((TextMessageChatTalkerUserRightVH) viewHolder).initLinkMessageChatTalkerUserRightVH(message);
                    ((TextMessageChatTalkerUserRightVH) viewHolder).clickToDeleteTextMessageUserRight(message, mTalkerId);
                    break;

                case VOICE:
                    ((VoiceMessageChatTalkerUserRightVH) viewHolder).initVoiceMessageChatTalkerUserRightVH(message);
                    ((VoiceMessageChatTalkerUserRightVH) viewHolder).clickToDeleteVoiceMessageUserRight(message, mTalkerId);
                    break;

                case VIDEO:
                    ((VideoMessageChatTalkerUserRightVH) viewHolder).initVideoMessageChatTalkerUserRightVH(message, mTalkerId);
                    ((VideoMessageChatTalkerUserRightVH) viewHolder).clickToDeleteVideoMessageUserRight(message, mTalkerId);
                    break;

                case IMAGE:
                    ((ImageMessageChatTalkerUserRightVH) viewHolder).initImageMessageChatTalkerUserRightVH(message, mTalkerId);
                    ((ImageMessageChatTalkerUserRightVH) viewHolder).clickToDeleteImageMessageUserRight(message, mTalkerId);
                    break;
            }
        }

    }
    /*@Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        String userId = mAuth.getCurrentUser().getUid();
        MessageUser message = mMessageList.get(position);

        mReferenceUser
                .child(message.getUidUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (message.getUidUser().equals(userId)) {
                            mCurrentUser = dataSnapshot.getValue(User.class);
                        }
                        else {
                            mUserRight = dataSnapshot.getValue(User.class);
                        }

                        if (message.getUidUser().equals(userId)) {
                            switch (message.getContentType()) {
                                case "TEXT":
                                    initTextMessageChatTalkerUserLeftVH((TextMessageChatTalkerUserLeftVH) viewHolder, message);
                                    clickToDeleteTextMessageUserLeft((TextMessageChatTalkerUserLeftVH)viewHolder, message);
                                    break;

                                case "LINK_INTO_MESSAGE":
                                    initLinkMessageChatTalkerUserLeftVH((TextMessageChatTalkerUserLeftVH) viewHolder, message);
                                    clickToDeleteTextMessageUserLeft((TextMessageChatTalkerUserLeftVH)viewHolder, message);
                                    break;

                                case "VOICE":
                                    initVoiceMessageChatTalkerUserLeftVH((VoiceMessageChatTalkerUserLeftVH) viewHolder, message, userId);
                                    clickToDeleteVoiceMessageUserLeft((VoiceMessageChatTalkerUserLeftVH) viewHolder, message);
                                    break;

                                case "VIDEO":
                                    initVideoMessageChatTalkerUserLeftVH((VideoMessageChatTalkerUserLeftVH) viewHolder, message, userId);
                                    clickToDeleteVideoMessageUserLeft((VideoMessageChatTalkerUserLeftVH) viewHolder, message);
                                    break;

                                case "IMAGE":
                                    initImageMessageChatTalkerUserLeftVH((ImageMessageChatTalkerUserLeftVH) viewHolder, message, userId);
                                    clickToDeleteImageMessageUserLeft((ImageMessageChatTalkerUserLeftVH) viewHolder, message);
                                    break;
                            }
                        }
                        else {
                            switch (message.getContentType()) {
                                case "TEXT":
                                    initTextMessageChatTalkerUserRightVH((TextMessageChatTalkerUserRightVH) viewHolder, message);
                                    clickToDeleteTextMessageUserRight((TextMessageChatTalkerUserRightVH)viewHolder, message);
                                    break;

                                case "LINK_INTO_MESSAGE":
                                    initLinkMessageChatTalkerUserRightVH((TextMessageChatTalkerUserRightVH) viewHolder, message);
                                    clickToDeleteTextMessageUserRight((TextMessageChatTalkerUserRightVH)viewHolder, message);
                                    break;

                                case "VOICE":
                                    initVoiceMessageChatTalkerUserRightVH((VoiceMessageChatTalkerUserRightVH) viewHolder, message);
                                    clickToDeleteVoiceMessageUserRight((VoiceMessageChatTalkerUserRightVH) viewHolder, message);
                                    break;

                                case "VIDEO":
                                    initVideoMessageChatTalkerUserRightVH((VideoMessageChatTalkerUserRightVH) viewHolder, message);
                                    clickToDeleteVideoMessageUserRight((VideoMessageChatTalkerUserRightVH) viewHolder, message);
                                    break;

                                case "IMAGE":
                                    initImageMessageChatTalkerUserRightVH((ImageMessageChatTalkerUserRightVH) viewHolder, message);
                                    clickToDeleteImageMessageUserRight((ImageMessageChatTalkerUserRightVH) viewHolder, message);
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });


    }*/


    //DELETE MESSAGES
    //user right
    private void clickToDeleteImageMessageUserRight(ImageMessageChatTalkerUserRightVH viewHolder, MessageUser message) {
        viewHolder.mRelativeImage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });
    }

    private void clickToDeleteVideoMessageUserRight(VideoMessageChatTalkerUserRightVH viewHolder, MessageUser message) {
        viewHolder.mRelativeVideo.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }

    private void clickToDeleteVoiceMessageUserRight(VoiceMessageChatTalkerUserRightVH viewHolder, MessageUser message) {
        viewHolder.mRelativeVoice.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }

    private void clickToDeleteTextMessageUserRight(TextMessageChatTalkerUserRightVH viewHolder, MessageUser message) {
        viewHolder.mCardViewTextMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }

    //user left
    private void clickToDeleteImageMessageUserLeft(ImageMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {
        viewHolder.mRelativeImage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }

    private void clickToDeleteVideoMessageUserLeft(VideoMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {

        viewHolder.mRelativeVideo.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }

    private void clickToDeleteTextMessageUserLeft(TextMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {
        //LISTENERS TO DELETE MESSAGES
        /*viewHolder.mCardViewTextMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });*/

    }

    private void clickToDeleteVoiceMessageUserLeft(VoiceMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {
        //LISTENERS TO DELETE MESSAGES
        viewHolder.mRelativeVoice.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message);
            }
        });

    }


    //INIT VIEW HOLDERS
    //user right
    private void initImageMessageChatTalkerUserRightVH(ImageMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        //SETUP VISIBILITIES
        viewHolder.mRelativeLoadingPanelImageUserRight.setVisibility(GONE);
        viewHolder.mProgressImageUserRight.setVisibility(GONE);
        viewHolder.mBtnDownloadImageUserRight.setVisibility(GONE);
        viewHolder.mTvImageSizeUserRight.setVisibility(GONE);

        //viewHolder.setTvMessageTimeImageUserRight(message.getTimestampCreatedLong());
        if (message.getImage().getDescription() != null) {
            viewHolder.mTvImageDescriptionUserRight.setVisibility(VISIBLE);
            viewHolder.setTvImageDescriptionRight(message.getImage().getDescription());
        } else {
            viewHolder.mTvImageDescriptionUserRight.setVisibility(GONE);
        }

        if (message.getImage().getDownloadUri() != null) {

            Uri imageUri = Uri.parse(message.getImage().getDownloadUri());

            setImageMessageUserRight(imageUri, viewHolder);

            viewHolder.mSdImageFrameUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowImageActivity(imageUri, message.getKey());
                }
            });
        }

    }

    private void initVideoMessageChatTalkerUserRightVH(VideoMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        //SETUP INCLUDE VISIBILITIES
        viewHolder.mBtnDownloadVideoUserRight.setVisibility(GONE);
        viewHolder.mRelativeLoadingPanelUserRight.setVisibility(VISIBLE);
        viewHolder.mProgressVideoUserRight.setVisibility(GONE);
        viewHolder.mTvVideoSizeUserRight.setVisibility(GONE);
        viewHolder.setTvDurationRight(message.getVideo().getDuration());
        //viewHolder.setTvSizeRight(String.valueOf(message.getVideo().getSize()));
        //viewHolder.setTvMessageTimeVideoUserRight(message.getTimestampCreatedLong());
        if (message.getVideo().getDescription() != null) {
            viewHolder.mTvVideoDescriptionUserRight.setVisibility(VISIBLE);
            viewHolder.setTvDescriptionRight(message.getVideo().getDescription());
        } else {
            viewHolder.mTvVideoDescriptionUserRight.setVisibility(GONE);
        }

        if (message.getVideo().getDownloadUri() != null) {

            Uri videUri = Uri.parse(message.getVideo().getDownloadUri());
            initPlayerUserRight(viewHolder, videUri);

            viewHolder.mRelativeLoadingPanelUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowVideoTalkerActivity(videUri, message.getKey());
                }
            });

            viewHolder.mBtnPlayVideoUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowVideoTalkerActivity(videUri, message.getKey());
                }
            });

        }
    }

    private void initVoiceMessageChatTalkerUserRightVH(VoiceMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        viewHolder.mBtnPlayAudioUserRight.setVisibility(VISIBLE);
        viewHolder.mBtnPauseAudioUserRight.setVisibility(GONE);
        viewHolder.mTvAudioStartUserRight.setVisibility(VISIBLE);
        viewHolder.mTvAudioEndUserRight.setVisibility(VISIBLE);
        viewHolder.mTvMessageTimeAudioUserRight.setVisibility(VISIBLE);
        viewHolder.mSeekBarVoiceMessageUserRight.setVisibility(VISIBLE);
        viewHolder.mCircleImageUserRightAudioMessage.setVisibility(VISIBLE);

        //SET MESSAGE TIME - AUDIO
        //viewHolder.setTvMessageTimeAudioUserRight(message.getTimestampCreatedLong());

        if (message.getAudio().getDuration() != null) {
            viewHolder.setAudioDurationUserRight(message.getAudio().getDuration());
        }
        downloadAudioUserRight(message, viewHolder);

    }

    private void initLinkMessageChatTalkerUserRightVH(TextMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        String link = message.getLink();
        String appendMessage = link + " " + message.getMessage();
        //SETUP VIEW
        viewHolder.setTvMessageUserRight(appendMessage);
        //viewHolder.setTvMessageTimeUserRight(message.getTimestampCreatedLong());

        setMessageLinkUserRight(link, viewHolder, message);

        viewHolder.mTvMessageUserRight.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void initTextMessageChatTalkerUserRightVH(TextMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        //SETUP VIEWS
        viewHolder.setTvMessageUserRight(message.getMessage());
        //viewHolder.setTvMessageTimeUserRight(message.getTimestampCreatedLong());

    }

    //user left
    private void initImageMessageChatTalkerUserLeftVH(ImageMessageChatTalkerUserLeftVH viewHolder, MessageUser message, String userId) {

        //SETUP VISIBILITIES
        viewHolder.mBtnDownloadImageUserLeft.setVisibility(GONE);

        //SETUP VIEW
        if (message.getImage().getDescription() != null) {
            viewHolder.mTvImageDescriptionUserLeft.setVisibility(VISIBLE);
            viewHolder.setTvImageDescriptionLeft(message.getImage().getDescription());
        } else {
            viewHolder.mTvImageDescriptionUserLeft.setVisibility(GONE);
        }
        //viewHolder.setTvMessageTimeImageUserLeft(message.getTimestampCreatedLong());

        if (message.getImage().isUploaded()) {
            downloadImage(message, viewHolder);

        } else {
            viewHolder.mProgressImageUserLeft.setVisibility(VISIBLE);
            uploadImageToFirebase(userId, mImageUri, viewHolder, mImagePath, message.getKey(), message);
        }

    }

    private void initVideoMessageChatTalkerUserLeftVH(VideoMessageChatTalkerUserLeftVH viewHolder, MessageUser message, String userId) {

        viewHolder.mRelativeLoadingPanelUserLeft.setVisibility(VISIBLE);

        //SETUPS
        viewHolder.setTvDurationLeft(message.getVideo().getDuration());

        if (message.getVideo().getDescription() != null) {
            viewHolder.mTvVideoDescriptionUserLeft.setVisibility(VISIBLE);
            viewHolder.setTvDescriptionLeft(message.getVideo().getDescription());
        } else {
            viewHolder.mTvVideoDescriptionUserLeft.setVisibility(GONE);
        }
        //viewHolder.setTvMessageTimeVideoUserLeft(message.getTimestampCreatedLong());

        viewHolder.mBtnDownloadVideoUserLeft.setVisibility(GONE);
        if (message.getVideo().isUploaded()) {

            downloadVideo(message, viewHolder);

        } else {
            viewHolder.mProgressVideoUserLeft.setVisibility(VISIBLE);
            uploadVideoToFirebase(userId, mVideoUri, viewHolder, mVideoPath, message.getKey(), message);
        }
    }

    private void initVoiceMessageChatTalkerUserLeftVH(VoiceMessageChatTalkerUserLeftVH viewHolder, MessageUser message, String userId) {
        if (message.getAudio().isUploaded()) {
            downloadAudio(message, viewHolder);
        } else {

            uploadAudiToFirebase2(userId, Uri.parse(message.getMessage()), viewHolder, message.getMessage(),
                    message.getKey(), message);
        }
    }

    private void initLinkMessageChatTalkerUserLeftVH(TextMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {

        String link = message.getLink();

        //SETUP VIEW
        //viewHolder.setTvMessageTimeUserLeft(message.getTimestampCreatedLong());

        setMessageLinkUserLeft(link, viewHolder, message);

    }

    private void initTextMessageChatTalkerUserLeftVH(TextMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {

        //SETUP VIEW
        viewHolder.setTvMessageUserLeft(message.getMessage());
        //viewHolder.setTvMessageTimeUserLeft(message.getTimestampCreatedLong());

    }

    //SET MESSAGE LINK_INTO_MESSAGE
    //user right
    private void setMessageLinkUserLeft(String link, TextMessageChatTalkerUserLeftVH viewHolder, MessageUser message) {

        int index = link.length();

        SpannableString linkStyled = new SpannableString(message.getMessage());
        int startLink = message.getMessage().indexOf(link);
        int endLink = index;
        try {
            endLink = message.getMessage().lastIndexOf(link.charAt(link.length() - 1));
        }
        catch (StringIndexOutOfBoundsException e){
            e.getLocalizedMessage();
        }

        linkStyled.setSpan(
                new URLSpan(link),
                startLink,
                endLink,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        viewHolder.setTvMessageLinkUserLeft(linkStyled);
        viewHolder.mTvMessageUserLeft.setMovementMethod(LinkMovementMethod.getInstance());

    }

    //user right
    private void setMessageLinkUserRight(String link, TextMessageChatTalkerUserRightVH viewHolder, MessageUser message) {

        int index = link.length();

        SpannableString linkStyled = new SpannableString(message.getMessage());
        int startLink = message.getMessage().indexOf(link);
        int endLink = index;
        try {
            endLink = message.getMessage().lastIndexOf(link.charAt(link.length() - 1));
        }
        catch (StringIndexOutOfBoundsException e){
            e.getLocalizedMessage();
        }

        linkStyled.setSpan(
                new URLSpan(link),
                startLink,
                endLink,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        viewHolder.setTvMessageLinkUserRight(linkStyled);
        viewHolder.mTvMessageUserRight.setMovementMethod(LinkMovementMethod.getInstance());


    }


    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(Context context, MessageUser message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");

        switch (message.getContentType()) {
            case TEXT:
            case LINK:
                builder.setMessage(message.getMessage());
                break;
            case VIDEO:
                builder.setMessage("Mensagem de vídeo");
                break;
            case VOICE:
                builder.setMessage("Mensagem de aúdio");
                break;
            case IMAGE:
                builder.setMessage("Mensagem com imagem");
                break;
        }

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteMessage(context, message);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMessage(Context context, MessageUser message) {

        switch (message.getContentType()){
            case TEXT:
            case LINK:
                deleteMessageTextOrLink(context, message);
                break;
            case VOICE:
            case VIDEO:
            case IMAGE:
                deleteMessageAudioOrVideoOrImage(context, message);
                break;

        }

    }

    private void deleteMessageAudioOrVideoOrImage(Context context, MessageUser message) {

        switch (message.getContentType()){
            case VIDEO:
                deleteVideo(context, message);
                break;

            case VOICE:
                deleteAudio(context, message);
                break;

            case IMAGE:
                deleteImage(context, message);
                break;
        }
    }

    private void deleteVideo(Context context, MessageUser message) {

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

    private void deleteAudio(Context context, MessageUser message) {

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

    private void deleteImage(Context context, MessageUser message) {

        StorageReference fileRef = mStorage.getReferenceFromUrl(message.getImage().getDownloadUri());

        fileRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            mRefTalkersMessage
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTalkerId)
                    .child(message.getKey())
                    .removeValue()
                    .addOnSuccessListener(aVoid2 -> {
                        showProgressDialog(false, null);
                        Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }).addOnFailureListener(e -> {
            e.getLocalizedMessage();
            showProgressDialog(false, null);
            Toast.makeText(context, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();

        });
    }


    private void deleteMessageTextOrLink(Context context, MessageUser message) {
        mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId)
                .child(message.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    showProgressDialog(false, null);
                    Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    showProgressDialog(false, null);
                    Toast.makeText(context, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();
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

    //IMAGE
    private void setImageMessageUserRight(Uri imageUri, ImageMessageChatTalkerUserRightVH viewHolder) {
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
                .setUri(imageUri)
                .setControllerListener(listener)
                .setOldController(viewHolder.mSdImageFrameUserRight.getController())
                .build();
        viewHolder.mSdImageFrameUserRight.setController(dc);
        viewHolder.mProgressImageUserRight.setVisibility(GONE);

    }


    //VIDEO
    private void initPlayerUserRight(VideoMessageChatTalkerUserRightVH viewHolder, Uri videoUri) {
        viewHolder.mSimplePlayerUserRight.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(viewHolder.mContext,
                Util.getUserAgent(viewHolder.mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ;

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        viewHolder.mExoplayer = ExoPlayerFactory.newSimpleInstance(viewHolder.mContext, trackSelector);
        viewHolder.mSimplePlayerUserRight.setPlayer(viewHolder.mExoplayer);
        viewHolder.mExoplayer.setPlayWhenReady(false);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        viewHolder.mExoplayer.prepare(mediaSource);

        viewHolder.mBtnPlayVideoUserRight.setVisibility(VISIBLE);
        viewHolder.mProgressVideoUserRight.setVisibility(GONE);

    }


    //USER LEFT
    //UPLOAD IMAGES TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadImageToFirebase(String userId, Uri mImageUri, ImageMessageChatTalkerUserLeftVH viewHolder,
                                       String mImagePath, String messageRef, MessageUser message) {

        //IMAGE MESSAGE INSIDE CURRENT TALKER
        DatabaseReference refMessage = mRefTalkersMessage
                .child(userId)
                .child(mTalkerId)
                .child(messageRef);

        //IMAGE MESSAGE INSIDE RIGHT TALKER
        DatabaseReference refMessageRightTalker = mRefTalkersMessage
                .child(mTalkerId)
                .child(userId)
                .child(messageRef);

        //DatabaseReference refMessage = mRefTalkersMessage.child(mTalkerId).child(messageRef);

        final StorageReference filePath = mStorageImagesTalkers
                .child(userId)
                .child("image")
                .child(mImagePath + "/image_message_left.mp4");

        filePath.putFile(mImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    String uri = filePath.getDownloadUrl().toString();
                    Map<String, Object> updateMessage = new HashMap<>();
                    updateMessage.put("uploaded", true);
                    updateMessage.put("downloadUri", uri);

                    //UPDATE IMAGE MESSAGE INSIDE CURRENT TALKER
                    refMessage
                            .child("image")
                            .updateChildren(updateMessage)
                            .addOnSuccessListener(aVoid -> {

                                //UPDATE IMAGE MESSAGE INSIDE RIGHT TALKER
                                refMessageRightTalker
                                        .child("image")
                                        .updateChildren(updateMessage)
                                        .addOnSuccessListener(aVoid1 -> {
                                            downloadImage(message, viewHolder);

                                            mReferenceUser
                                                    .child(mTalkerId)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            User talkerUser = dataSnapshot.getValue(User.class);

                                                            if (!talkerUser.isOnlineInChat()) {
                                                                updateNumUnreadMessages(mTalkerId);

                                                                updateChatTalker("Mensagem com Imagem");
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
                    Toast.makeText(mContext, "Erro ao enviar imagem.", Toast.LENGTH_LONG).show();
                    refMessage.removeValue();

                })
                .addOnProgressListener(taskSnapshot -> {
                    //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();;
                    //System.out.println("Envio " + progress + "% concluído");
                    int currentprogress = (int) progress;

                    viewHolder.mProgressImageUserLeft.setProgress(currentprogress);

                });


    }

    private void updateChatTalker(String message) {
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


    //SET IMAGE
    private void setImageMessage(Uri imageUri, ImageMessageChatTalkerUserLeftVH viewHolder) {
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
                .setUri(imageUri)
                .setControllerListener(listener)
                .setOldController(viewHolder.mSdImageFrameUserLeft.getController())
                .build();
        viewHolder.mSdImageFrameUserLeft.setController(dc);
        viewHolder.mProgressImageUserLeft.setVisibility(GONE);

    }

    //UPLOAD VIDEOS TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadVideoToFirebase(String userId, Uri mVideoUri, VideoMessageChatTalkerUserLeftVH viewHolder,
                                       String mVideoPath, String messageRef, MessageUser message) {

        //VIDEO MESSAGE INSIDE CURRENT TALKER
        DatabaseReference refMessage = mRefTalkersMessage
                .child(userId)
                .child(mTalkerId)
                .child(messageRef);


        //VIDEO MESSAGE INSIDE RIGHT TALKER
        DatabaseReference refMessageRightTalker = mRefTalkersMessage
                .child(mTalkerId)
                .child(userId)
                .child(messageRef);

        final StorageReference filePath = mStorageVideosUsers
                .child(userId)
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
                                            downloadVideo(message, viewHolder);

                                            mReferenceUser
                                                    .child(mTalkerId)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            User talkerUser = dataSnapshot.getValue(User.class);

                                                            if (!talkerUser.isOnlineInChat()) {
                                                                updateNumUnreadMessages(mTalkerId);

                                                                updateChatTalker("Mensagem de Vídeo");
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
                    viewHolder.mProgressVideoUserLeft.setProgress(currentprogress);


                    //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                });

    }

    //INIT VIDEO
    private void initPlayer(VideoMessageChatTalkerUserLeftVH viewHolder, Uri videoUri) {
        viewHolder.mSimplePlayerUserLeft.requestFocus();

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DefaultTrackSelector trackSelector;
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(viewHolder.mContext,
                Util.getUserAgent(viewHolder.mContext, "Tribus"),
                (TransferListener<? super DataSource>) bandwidthMeter);

        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        viewHolder.mExoplayer = ExoPlayerFactory.newSimpleInstance(viewHolder.mContext, trackSelector);
        viewHolder.mSimplePlayerUserLeft.setPlayer(viewHolder.mExoplayer);
        viewHolder.mExoplayer.setPlayWhenReady(false);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(videoUri,
                mediaDataSourceFactory, extractorsFactory, null, null);
        viewHolder.mExoplayer.prepare(mediaSource);
        viewHolder.mBtnPlayVideoUserLeft.setVisibility(VISIBLE);
        viewHolder.mProgressVideoUserLeft.setVisibility(GONE);

    }

    //UPLOAD AUDIO TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadAudiToFirebase2(String userId, Uri mAudioUri, VoiceMessageChatTalkerUserLeftVH viewHolder,
                                       String mAudioPath, String messageRef, MessageUser message) {

        //VIDEO MESSAGE INSIDE CURRENT TALKER
        DatabaseReference refMessage = mRefTalkersMessage
                .child(userId)
                .child(mTalkerId)
                .child(messageRef);


        //VIDEO MESSAGE INSIDE RIGHT TALKER
        DatabaseReference refMessageRightTalker = mRefTalkersMessage
                .child(mTalkerId)
                .child(userId)
                .child(messageRef);

        viewHolder.mProgressLoadingAudio.setVisibility(VISIBLE);
        viewHolder.mBtnPlayAudioUserLeft.setVisibility(GONE);
        viewHolder.mBtnPauseAudioUserLeft.setVisibility(GONE);
        viewHolder.mTvAudioStartUserLeft.setVisibility(GONE);
        viewHolder.mTvAudioEndUserLeft.setVisibility(GONE);
        viewHolder.mTvMessageTimeAudioUserLeft.setVisibility(GONE);
        viewHolder.mSeekBarVoiceMessageUserLeft.setVisibility(GONE);


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
                                    downloadAudio(message, viewHolder);

                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);

                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }).addOnFailureListener(Throwable::getLocalizedMessage);

    }


    //COMMON CODE
    private void downloadAudio(MessageUser message,
                               VoiceMessageChatTalkerUserLeftVH viewHolder) {

        if (message.getAudio().getDownloadUri() != null) {

            viewHolder.mProgressLoadingAudio.setVisibility(GONE);
            viewHolder.mBtnPlayAudioUserLeft.setVisibility(VISIBLE);
            viewHolder.mBtnPauseAudioUserLeft.setVisibility(GONE);
            viewHolder.mTvAudioStartUserLeft.setVisibility(VISIBLE);
            viewHolder.mTvAudioEndUserLeft.setVisibility(VISIBLE);
            viewHolder.mTvMessageTimeAudioUserLeft.setVisibility(VISIBLE);
            viewHolder.mSeekBarVoiceMessageUserLeft.setVisibility(VISIBLE);

            //SET MESSAGE TIME - AUDIO
            //viewHolder.setTvMessageTimeAudioUserLeft(message.getTimestampCreatedLong());

            if(message.getAudio().getDuration() != null){
                viewHolder.setAudioDurationUserLeft(message.getAudio().getDuration());
            }

            viewHolder.initPlayer(0, Uri.parse(message.getAudio().getDownloadUri()));

            viewHolder.mBtnPlayAudioUserLeft.setOnClickListener(v -> {

                viewHolder.initPlayButton();

            });

            viewHolder.mBtnPauseAudioUserLeft.setOnClickListener(v -> {
                viewHolder.initPauseButton();
            });


        }

    }


    private void downloadAudioUserRight(MessageUser message,
                                        VoiceMessageChatTalkerUserRightVH viewHolder) {

        if (message.getAudio().getDownloadUri() != null) {

            Uri audioUri = Uri.parse(message.getAudio().getDownloadUri());

            viewHolder.initPlayerUserRight(0, audioUri);

            viewHolder.mBtnPlayAudioUserRight.setOnClickListener(v -> {

                viewHolder.initPlayButtonUserRight();

            });

            viewHolder.mBtnPauseAudioUserRight.setOnClickListener(v -> {
                viewHolder.initPauseButtonUserRight();
            });

        }
    }


    private void downloadImage(MessageUser message, ImageMessageChatTalkerUserLeftVH viewHolder) {

        if (message.getImage().getDownloadUri() != null) {
            Uri imageUrl = Uri.parse(message.getImage().getDownloadUri());
            setImageMessage(imageUrl, viewHolder);
            viewHolder.mSdImageFrameUserLeft.setOnClickListener(v -> {
                openShowImageActivity(imageUrl, message.getKey());

            });
        }
    }


    private void downloadVideo(MessageUser message, VideoMessageChatTalkerUserLeftVH viewHolder) {

        if (message.getVideo().getDownloadUri() != null) {

            Uri videoUri = Uri.parse(message.getVideo().getDownloadUri());
            initPlayer(viewHolder, videoUri);
            viewHolder.mRelativeLoadingPanelUserLeft.setOnClickListener(v -> {
                openShowVideoTalkerActivity(videoUri, message.getKey());
            });

            viewHolder.mBtnPlayVideoUserLeft.setOnClickListener(v -> {
                openShowVideoTalkerActivity(videoUri, message.getKey());
            });

        }
    }


    private void openShowVideoTalkerActivity(Uri uri, String ref) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowVideoTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }


    private void openShowImageActivity(Uri uri, String ref) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowImageTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }
}
