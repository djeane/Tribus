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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.REMOVED;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 12/13/2017.
 */

public class ReplyTextMessageChatTribuUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.layout_row_reply_text_user_left)
    RelativeLayout mLayoutRowReplyTextUserLeft;

    @BindView(R.id.tv_message_user_left)
    public TextView mTvMessageUserLeft;

    @BindView(R.id.message_time_user_left)
    public TextView mTvMessageTimeUserLeft;

    @BindView(R.id.iv_garbage_message)
    ImageView mIvGarbageMessage;

    @BindView(R.id.relative_reply_message)
    RelativeLayout mRelativeReplyMessage;

    @BindView(R.id.tv_reply_message)
    TextView mTvReplyMessage;

    @BindView(R.id.tv_reply_username)
    TextView mTvReplyUsername;


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefTribusMessage;

    //SHOW PROGRESS
    private ProgressDialog progress;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mTribusCollection;
    private CollectionReference mUsersCollection;


    public ReplyTextMessageChatTribuUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mUsersCollection = mFirestore.collection(GENERAL_USERS);

        mDatabase = FirebaseDatabase.getInstance();
        mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

    }

    // TEXT MESSAGE
    //set message
    public void setTvMessageUserLeft(String message) {
        mTvMessageUserLeft.setText(message);
    }

    public void setTvMessageLinkUserLeft(SpannableString message) {
        mTvMessageUserLeft.setText(message);
    }


    //set time text message
    private void setTvMessageTimeUserLeft(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeUserLeft.setText(time);
    }

    private void setTvReplyMessage(String message){
        mTvReplyMessage.setText(message);
    }

    private void setTvReplyUsername(String name, String username){
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";

        mTvReplyUsername.setText(appendNameAndUsername);
    }

    public void initReplyTextMessageChatTribuUserLeftVH(MessageUser message,
                                                        ChatTribuView mView,
                                                        String mTribusKey, Boolean mIsPublic) {

        mUsersCollection
                .document(message.getReplyUserId())
                .addSnapshotListener(mView.mContext, (documentSnapshot, e) -> {

                    if (e != null){
                        e.printStackTrace();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()){

                        User user = documentSnapshot.toObject(User.class);
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
                        setTvReplyUsername(user.getName(), user.getUsername());
                        setTvReplyMessage(message.getReplyMessage());
                    }

                    mLayoutRowReplyTextUserLeft.setVisibility(View.VISIBLE);
                });
    }

    public void initLinkMessageChatTribuUserLeftVH(MessageUser message,
                                                    ChatTribuView mView,
                                                    String mTribusKey) {
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


        Map<String, Object> updateTypeMessageToRemoved = new HashMap<>();
        updateTypeMessageToRemoved.put("contentType", REMOVED);

        mTribusCollection
                .document(mTribusKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .update(updateTypeMessageToRemoved)

                /*mTribusCollection
                .document(mTribusUniqueName)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .delete()*/
                .addOnSuccessListener(aVoid -> {
                    showProgressDialog(false, null);
                    /*Toast.makeText(context, "A mensagem foi removida",
                            Toast.LENGTH_LONG).show();*/
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    showProgressDialog(false, null);
                    Toast.makeText(context, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();

                });
    }

    /*private void deleteMessageTextOrLink(Context context, MessageUser message, String mTribusUniqueName) {
        mRefTribusMessage
                .child(mTribusUniqueName)
                .child(message.getTopicKey())
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
    }*/


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
