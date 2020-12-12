package apptribus.com.tribus.activities.chat_user.view_holder.user_right;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image_talker.ShowImageTalkerActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.TALKERS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 12/14/2017.
 */

public class ImageMessageChatTalkerUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    //image message
    @BindView(R.id.layout_image_user_right)
    public RelativeLayout mRelativeImage;

    @BindView(R.id.relative_simple_drawee_view_user_right)
    public RelativeLayout mRelativeSimpleDraweeUserRight;

    @BindView(R.id.image_frame_user_right)
    public SimpleDraweeView mSdImageFrameUserRight;

    @BindView(R.id.circle_user_image_right_image_message)
    public SimpleDraweeView mCircleImageUserRightImageMessage;

    @BindView(R.id.loading_painel_image_user_right)
    public RelativeLayout mRelativeLoadingPanelImageUserRight;

    @BindView(R.id.progress_image_user_right)
    public ProgressBar mProgressImageUserRight;

    @BindView(R.id.btn_download_image_user_right)
    public ImageButton mBtnDownloadImageUserRight;

    @BindView(R.id.linear_image_info_user_right)
    public LinearLayout mLinearImageInfoUserRight;

    @BindView(R.id.tv_size_image_user_right)
    public TextView mTvImageSizeUserRight;

    @BindView(R.id.relative_image_description_user_right)
    public RelativeLayout mRelativeImageDescriptionUserRight;

    @BindView(R.id.tv_image_description_user_right)
    public TextView mTvImageDescriptionUserRight;

    @BindView(R.id.message_time_image_user_right)
    public TextView mTvMessageTimeImageUserRight;

    @BindView(R.id.tv_username_image)
    public TextView mTvUserNameImage;


    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private DatabaseReference mRefTalkersMessage;
    private StorageReference mStorageImagesTalkers;

    //SHOW
    private ProgressDialog progress;


    public ImageMessageChatTalkerUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageImagesTalkers = mStorage.getReference().child(TALKERS_IMAGES_MESSAGES);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
        mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

    }

    //IMAGE MESSAGE
    public void setTvImageDescriptionRight(String description){
        mTvImageDescriptionUserRight.setText(description);
    }

    public void setTvMessageTimeImageUserRight(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeImageUserRight.setText(time);
    }


    //INIT VIEW HOLDERS
    //user right
    public void initImageMessageChatTalkerUserRightVH(MessageUser message, String mTalkerId) {

        //SETUP VISIBILITIES
        mRelativeLoadingPanelImageUserRight.setVisibility(GONE);
        mProgressImageUserRight.setVisibility(GONE);
        mBtnDownloadImageUserRight.setVisibility(GONE);
        mTvImageSizeUserRight.setVisibility(GONE);

        //setTvMessageTimeImageUserRight(message.getTimestampCreatedLong());
        if (message.getImage().getDescription() != null) {
            mTvImageDescriptionUserRight.setVisibility(VISIBLE);
            setTvImageDescriptionRight(message.getImage().getDescription());
        } else {
            mTvImageDescriptionUserRight.setVisibility(GONE);
        }

        if (message.getImage().getDownloadUri() != null) {

            Uri imageUri = Uri.parse(message.getImage().getDownloadUri());

            setImageMessageUserRight(imageUri);

            mSdImageFrameUserRight.setOnClickListener(v -> {
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
                } else {
                    openShowImageActivity(imageUri, message.getKey(), mTalkerId);
                }
            });
        }

    }

    //DELETE MESSAGES
    //user right
    public void clickToDeleteImageMessageUserRight(MessageUser message, String mTalkerId) {
        mRelativeImage.setOnClickListener(v -> {
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
        builder.setMessage("Mensagem com imagem");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida...");
                deleteImage(message, mTalkerId);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteImage(MessageUser message, String mTalkerId) {

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
                        Toast.makeText(mContext, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.", Toast.LENGTH_LONG).show();
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


    //IMAGE
    private void setImageMessageUserRight(Uri imageUri) {
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
                .setOldController(mSdImageFrameUserRight.getController())
                .build();
        mSdImageFrameUserRight.setController(dc);
        mProgressImageUserRight.setVisibility(GONE);

    }


    private void openShowImageActivity(Uri uri, String ref, String mTalkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowImageTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }

}
