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
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;

/**
 * Created by User on 12/13/2017.
 */

public class RemovedMessageChatTribuUserLeftVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.relative_row_removed_message_user_left)
    RelativeLayout mRelativeRowRemovedMessageUserLeft;

    @BindView(R.id.message_time_user_left)
    TextView mTvMessageTimeUserLeft;


    public RemovedMessageChatTribuUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();

    }


    //set time text message
    private void setTvMessageTimeUserLeft(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeUserLeft.setText(time);
    }

    public void initTextMessageChatTribuUserLeftVH(MessageUser message,
                                                   ChatTribuView mView,
                                                   String mTribusKey, Boolean mIsPublic) {

        setTvMessageTimeUserLeft(message.getDate());
        mRelativeRowRemovedMessageUserLeft.setVisibility(VISIBLE);
    }


}
