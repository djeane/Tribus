package apptribus.com.tribus.activities.chat_user.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 12/14/2017.
 */

public class TextMessageChatTalkerUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    //text message
    @BindView(R.id.card_view_text_message)
    public CardView mCardViewTextMessage;

    @BindView(R.id.tv_message_user_right)
    public TextView mTvMessageUserRight;

    @BindView(R.id.circle_user_image_right)
    public SimpleDraweeView mCircleImageUserRight;

    @BindView(R.id.message_time_user_right)
    public TextView mTvMessageTimeUserRight;

    @BindView(R.id.tv_username)
    TextView mTvUserName;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private DatabaseReference mRefTalkersMessage;
    private DatabaseReference mRefChatTalker;

    //SHOW
    private ProgressDialog progress;


    public TextMessageChatTalkerUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

    }

    //TEXT MESSAGE
    //set message
    public void setTvMessageUserRight(String message) {
        mTvMessageUserRight.setText(message);
    }

    public void setTvMessageLinkUserRight(SpannableString message) {
        mTvMessageUserRight.setText(message);
    }

    //set time text message
    public void setTvMessageTimeUserRight(long timestamp) {
        //SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeUserRight.setText(time);
    }


    public void initTextMessageChatTalkerUserRightVH(MessageUser message) {

        //SETUP VIEWS
        setTvMessageUserRight(message.getMessage());
        //setTvMessageTimeUserRight(message.getTimestampCreatedLong());

    }

    public void clickToDeleteTextMessageUserRight(MessageUser message, String mTalkerId) {
        mCardViewTextMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(message, mTalkerId);
            }
        });

    }

    public void initLinkMessageChatTalkerUserRightVH(MessageUser message) {

        String link = message.getLink();
        String appendMessage = link + " " + message.getMessage();
        //SETUP VIEW
        setTvMessageUserRight(appendMessage);
        //setTvMessageTimeUserRight(message.getTimestampCreatedLong());

        setMessageLinkUserRight(link, message);

        mTvMessageUserRight.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setMessageLinkUserRight(String link, MessageUser message) {

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

        setTvMessageLinkUserRight(linkStyled);
        mTvMessageUserRight.setMovementMethod(LinkMovementMethod.getInstance());


    }

    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(MessageUser message, String mTalkerId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage(message.getMessage());

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteMessageTextOrLink(message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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


    private void deleteMessageTextOrLink(MessageUser message, String mTalkerId) {
        mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId)
                .child(message.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    showProgressDialog(false, null);
                    Toast.makeText(mContext, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                            Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    showProgressDialog(false, null);
                    Toast.makeText(mContext, "Falha ao remover mensagem!", Toast.LENGTH_SHORT).show();
                });
    }


}
