package apptribus.com.tribus.activities.chat_tribu.view_holder.user_left;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.REMOVED;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 12/13/2017.
 */

public class TextMessageChatTribuUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.layout_row_text_user_left)
    RelativeLayout mLayoutRowTextUserLeft;

    @BindView(R.id.relative_message)
    RelativeLayout mRelativeMessage;

    @BindView(R.id.tv_message_user_left)
    public TextView mTvMessageUserLeft;

    @BindView(R.id.message_time_user_left)
    public TextView mTvMessageTimeUserLeft;

    @BindView(R.id.iv_garbage_message)
    public ImageView mIvGarbageMessage;

    @BindView(R.id.relative_removed_message_user_left)
    public RelativeLayout mRelativeRemovedMessageUserLeft;

    @BindView(R.id.message_time_removed_message_user_left)
    public TextView mTvMessageTimeRemovedMessageUserLeft;

    @BindView(R.id.sv_tag)
    SimpleDraweeView mSvTag;

    @BindView(R.id.tv_tag_num)
    TextView mTvTagNum;


    //FIREBASE
    //private FirebaseAuth mAuth;

    //SHOW PROGRESS
    private ProgressDialog progress;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mTribusCollection;


    public TextMessageChatTribuUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        //mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);

        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

    }

    // TEXT MESSAGE
    //set message
    private void setTvMessageUserLeft(String message) {
        mTvMessageUserLeft.setText(message);
    }

    private void setTvMessageLinkUserLeft(SpannableString message) {
        mTvMessageUserLeft.setText(message);
    }


    //set time text message
    private void setTvMessageTimeUserLeft(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeUserLeft.setText(time);
        mTvMessageTimeRemovedMessageUserLeft.setText(time);
    }

    public void initTextMessageChatTribuUserLeftVH(MessageUser message,
                                                   ChatTribuView mView,
                                                   String mTribusKey, Boolean mIsPublic) {


        if (mIsPublic) {
            mSvTag.setVisibility(VISIBLE);
            mTvTagNum.setVisibility(VISIBLE);
        }
        else {
            mSvTag.setVisibility(GONE);
            mTvTagNum.setVisibility(GONE);
        }

        //mLayoutRowTextUserLeft.setVisibility(View.VISIBLE);

        mTribusCollection
                .document(mTribusKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .addSnapshotListener(mView.mContext, (documentSnapshot, e) -> {

                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {

                        MessageUser newMessage = documentSnapshot.toObject(MessageUser.class);

                        if (mIsPublic) {
                            mSvTag.setVisibility(VISIBLE);
                            mTvTagNum.setVisibility(VISIBLE);
                        }
                        else {
                            mSvTag.setVisibility(GONE);
                            mTvTagNum.setVisibility(GONE);
                        }

                        if (newMessage.getContentType().equals(REMOVED)) {
                            mIvGarbageMessage.setVisibility(View.GONE);
                            mRelativeMessage.setVisibility(View.GONE);
                            mRelativeRemovedMessageUserLeft
                                    .setVisibility(View.VISIBLE);
                        }
                        else {
                            mIvGarbageMessage.setVisibility(View.VISIBLE);
                            mRelativeMessage.setVisibility(View.VISIBLE);
                            mRelativeRemovedMessageUserLeft
                                    .setVisibility(View.GONE);
                        }


                        mLayoutRowTextUserLeft.setVisibility(View.VISIBLE);
                    }

                });


        //LISTENERS TO DELETE MESSAGES
        mIvGarbageMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                showDialogToDeleteMessage(mContext, message, mView, mTribusKey);
            }
        });

        //SETUP VIEW
        setTvMessageUserLeft(message.getMessage());
        setTvMessageTimeUserLeft(message.getDate());


    }

    public void initLinkMessageChatTribuUserLeftVH(MessageUser message,
                                                   ChatTribuView mView,
                                                   String mTribusKey, Boolean mIsPublic) {

        if (mIsPublic) {
            mSvTag.setVisibility(VISIBLE);
            mTvTagNum.setVisibility(VISIBLE);
        } else {
            mSvTag.setVisibility(GONE);
            mTvTagNum.setVisibility(GONE);
        }


        //LISTENERS TO DELETE MESSAGES
            mIvGarbageMessage.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, mView);
                    showDialogToDeleteMessage(mContext, message, mView, mTribusKey);
                }
            });


            String link = message.getLink();

            //SETUP VIEW
            setTvMessageTimeUserLeft(message.getDate());

            setMessageLinkUserLeft(link, message);

        mLayoutRowTextUserLeft.setVisibility(View.VISIBLE);
    }

    private void setMessageLinkUserLeft(String link, MessageUser message) {

        int index = link.length();

        SpannableString linkStyled = new SpannableString(message.getMessage());
        int startLink = message.getMessage().indexOf(link);
        int endLink = index;
        try {
            endLink = message.getMessage().lastIndexOf(link.charAt(link.length() - 1));
        } catch (StringIndexOutOfBoundsException e) {
            e.getLocalizedMessage();
        }

        linkStyled.setSpan(
                new URLSpan(link),
                startLink,
                endLink,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        setTvMessageLinkUserLeft(linkStyled);
        mTvMessageUserLeft.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void showDialogToDeleteMessage(Context context, MessageUser message, ChatTribuView mView, String mTribusKey) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage(message.getMessage());

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida... ");
                deleteMessageTextOrLink(context, message, mTribusKey);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMessageTextOrLink(Context context, MessageUser message, String mTribusKey) {

            //mIvGarbageMessage.setVisibility(View.GONE);
            //mRelativeMessage.setVisibility(View.GONE);
            //mRelativeRemovedMessageUserLeft.setVisibility(View.VISIBLE);

            Map<String, Object> updateTypeMessageToRemoved = new HashMap<>();
            updateTypeMessageToRemoved.put("contentType", REMOVED);

            mTribusCollection
                    .document(mTribusKey)
                    .collection(TOPICS)
                    .document(message.getTopicKey())
                    .collection(TOPIC_MESSAGES)
                    .document(message.getKey())
                    .update(updateTypeMessageToRemoved)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false, null);

                    })
                    .addOnFailureListener(e -> {
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

}
