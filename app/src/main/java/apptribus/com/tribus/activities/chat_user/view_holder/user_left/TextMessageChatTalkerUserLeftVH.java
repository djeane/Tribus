package apptribus.com.tribus.activities.chat_user.view_holder.user_left;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_user.mvp.ChatUserView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 12/14/2017.
 */

public class TextMessageChatTalkerUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    /*@BindView(R.id.card_view_text_message)
    public CardView mCardViewTextMessage;*/

    @BindView(R.id.layout_row_text_user_left)
    RelativeLayout mLayoutRowTextUserLeft;

    @BindView(R.id.iv_garbage_message)
    ImageView mIvGarbageMessage;


    @BindView(R.id.tv_message_user_left)
    public TextView mTvMessageUserLeft;

    @BindView(R.id.message_time_user_left)
    public TextView mTvMessageTimeUserLeft;


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRefTalkersMessage;


    //SHOW PROGRESS
    private ProgressDialog progress;


    public TextMessageChatTalkerUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        mLayoutRowTextUserLeft.setVisibility(View.VISIBLE);

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
    public void setTvMessageTimeUserLeft(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeUserLeft.setText(time);
    }

    public void initTextMessageChatTalkerUserLeftVH(MessageUser message) {

        //SETUP VIEW
        setTvMessageUserLeft(message.getMessage());
        //setTvMessageTimeUserLeft(message.getTimestampCreatedLong());

    }

    public void clickToDeleteTextMessageUserLeft(MessageUser message, String mTalkerId) {
        //LISTENERS TO DELETE MESSAGES
        mIvGarbageMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                showDialogToDeleteMessage(mContext, message, mTalkerId);
            }
        });

    }

    public void initLinkMessageChatTalkerUserLeftVH(MessageUser message) {

        String link = message.getLink();

        //SETUP VIEW
        //setTvMessageTimeUserLeft(message.getTimestampCreatedLong());

        setMessageLinkUserLeft(link, message);

    }

    private void setMessageLinkUserLeft(String link, MessageUser message) {

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

        setTvMessageLinkUserLeft(linkStyled);
        mTvMessageUserLeft.setMovementMethod(LinkMovementMethod.getInstance());

    }



    private void showDialogToDeleteMessage(Context context, MessageUser message, String mTalkerId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage(message.getMessage());

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteMessageTextOrLink(context, message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMessageTextOrLink(Context context, MessageUser message, String mTalkerId) {
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
