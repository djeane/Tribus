package apptribus.com.tribus.activities.chat_tribu.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuPresenter;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.ImageMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.RemovedMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.ReplyTextMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.TextMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.VideoMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_left.VoiceMessageChatTribuUserLeftVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.ImageMessageChatTribuUserRightVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.RemovedMessageChatTribuUserRightVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.TextMessageChatTribuUserRightVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.VideoMessageChatTribuUserRightVH;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.VoiceMessageChatTribuUserRightVH;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;

import static apptribus.com.tribus.util.Constantes.IMAGE;
import static apptribus.com.tribus.util.Constantes.IMAGE_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.IMAGE_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.LINK;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REPLY;
import static apptribus.com.tribus.util.Constantes.REMOVED;
import static apptribus.com.tribus.util.Constantes.REMOVED_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.REMOVED_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.REPLY_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TEXT_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.TEXT_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.VIDEO;
import static apptribus.com.tribus.util.Constantes.VIDEO_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.VIDEO_MESSAGE_USER_RIGHT;
import static apptribus.com.tribus.util.Constantes.VOICE;
import static apptribus.com.tribus.util.Constantes.VOICE_MESSAGE_USER_LEFT;
import static apptribus.com.tribus.util.Constantes.VOICE_MESSAGE_USER_RIGHT;

public class ChatTribuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MessageUser> mMessageList;
    private String mTribusKey;
    private Uri mVideoUri;
    private String mVideoPath;
    private Uri mImageUri;
    private String mImagePath;
    private Tribu mTribu;
    private ChatTribuView mView;
    private Boolean mIsPublic;
    public static TextMessageChatTribuUserRightVH.TextMessageChatTribuUserRightListener mListener;


    //SHOW
    private ProgressDialog progress;

    private File mAudioFolder;
    private User mUserRight;
    private User mCurrentUser;
    private String topicKey;

    //FIREBASE
    private FirebaseAuth mAuth;


    public ChatTribuAdapter(Context context, List<MessageUser> mMessageList, String mTribusKey, Uri mVideoUri,
                            String mVideoPath, Uri mImageUri, String mImagePath, ChatTribuView view,
                            String topicKey, ChatTribuPresenter presenter, Boolean isPublic) {

        mAuth = FirebaseAuth.getInstance();

        mContext = context;
        this.mMessageList = mMessageList;
        this.mTribusKey = mTribusKey;
        this.mVideoUri = mVideoUri;
        this.mVideoPath = mVideoPath;
        this.mImageUri = mImageUri;
        this.mImagePath = mImagePath;
        this.mView = view;
        this.topicKey = topicKey;
        this.mIsPublic = isPublic;
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        if (presenter != null){
            mListener = presenter;
        }

        //setHasStableIds(true);
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

            if(message != null){
                if (mAuth.getCurrentUser() != null && (message.getUidUser().equals(mAuth.getCurrentUser().getUid()))) {
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
                        case REMOVED:
                            return REMOVED_MESSAGE_USER_LEFT;
                        case MESSAGE_REPLY:
                            return REPLY_MESSAGE_USER_LEFT;

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
                        case REMOVED:
                            return REMOVED_MESSAGE_USER_RIGHT;

                        default:
                            return -1;
                    }
                }
            }
            else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TEXT_MESSAGE_USER_LEFT:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text_message_user_left, parent, false);
                return new TextMessageChatTribuUserLeftVH(v);

            case VOICE_MESSAGE_USER_LEFT:
                View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_message_user_left, parent, false);
                return new VoiceMessageChatTribuUserLeftVH(v1);

            case VIDEO_MESSAGE_USER_LEFT:
                View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_message_left, parent, false);
                return new VideoMessageChatTribuUserLeftVH(v2);

            case IMAGE_MESSAGE_USER_LEFT:
                View v3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_user_left, parent, false);
                return new ImageMessageChatTribuUserLeftVH(v3);

            case TEXT_MESSAGE_USER_RIGHT:
                View v4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text_message_user_right_tribu, parent, false);
                return new TextMessageChatTribuUserRightVH(v4);

            case VOICE_MESSAGE_USER_RIGHT:
                View v5 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_audio_message_user_right_tribu, parent, false);
                return new VoiceMessageChatTribuUserRightVH(v5);

            case VIDEO_MESSAGE_USER_RIGHT:
                View v6 = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_message_right_tribu, parent, false);
                return new VideoMessageChatTribuUserRightVH(v6);

            case IMAGE_MESSAGE_USER_RIGHT:
                View v7 = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_message_user_right_tribu, parent, false);
                return new ImageMessageChatTribuUserRightVH(v7);

            case REMOVED_MESSAGE_USER_RIGHT:
                Log.e("tribuss: ", "foi no ChatAdapter setar layout de mensagem removida user RIGHT no onCreateViewHolder");
                View v8 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_removed_message_user_right_tribu, parent, false);
                return new RemovedMessageChatTribuUserRightVH(v8);

            case REMOVED_MESSAGE_USER_LEFT:
                Log.e("tribuss: ", "foi no ChatAdapter setar layout de mensagem removida user LEFT no onCreateViewHolder");
                View v9 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_removed_message_user_left, parent, false);
                return new RemovedMessageChatTribuUserLeftVH(v9);

            case REPLY_MESSAGE_USER_LEFT:
                View v10 = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_reply_text_message_user_left, parent, false);
                return new ReplyTextMessageChatTribuUserLeftVH(v10);

            default:
                throw new RuntimeException("Não há layout para essa view.");
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        MessageUser message = mMessageList.get(position);

        if (message.getUidUser().equals(mAuth.getCurrentUser().getUid())){

            switch (message.getContentType()) {
                case TEXT:
                    ((TextMessageChatTribuUserLeftVH) viewHolder).initTextMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case LINK:
                    ((TextMessageChatTribuUserLeftVH) viewHolder).initLinkMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case VOICE:
                    ((VoiceMessageChatTribuUserLeftVH) viewHolder).initVoiceMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case VIDEO:
                    ((VideoMessageChatTribuUserLeftVH) viewHolder).initVideoMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mVideoUri, mVideoPath, mIsPublic);
                    break;

                case IMAGE:
                    ((ImageMessageChatTribuUserLeftVH) viewHolder).initImageMessageChatTribuUserLeftVH(message, mView, mTribusKey,
                            mImageUri, mImagePath, mIsPublic);
                    break;

                case REMOVED:
                    ((RemovedMessageChatTribuUserLeftVH) viewHolder).initTextMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case MESSAGE_REPLY:
                    ((ReplyTextMessageChatTribuUserLeftVH) viewHolder).initReplyTextMessageChatTribuUserLeftVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;
            }
        }
        else {
            switch (message.getContentType()) {
                case TEXT:
                    ((TextMessageChatTribuUserRightVH) viewHolder).initTextMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case LINK:
                    ((TextMessageChatTribuUserRightVH) viewHolder).initLinkMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case VOICE:
                    ((VoiceMessageChatTribuUserRightVH) viewHolder).initVoiceMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case VIDEO:
                    ((VideoMessageChatTribuUserRightVH) viewHolder).initVideoMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case IMAGE:
                    ((ImageMessageChatTribuUserRightVH) viewHolder).initImageMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;

                case REMOVED:
                    ((RemovedMessageChatTribuUserRightVH) viewHolder).initRemovedMessageChatTribuUserRightVH(message, mView,
                            mTribusKey, mIsPublic);
                    break;
            }
        }
    }

}
