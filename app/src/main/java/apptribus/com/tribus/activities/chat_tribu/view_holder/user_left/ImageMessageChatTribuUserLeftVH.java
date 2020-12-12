package apptribus.com.tribus.activities.chat_tribu.view_holder.user_left;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.show_image.ShowImageActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.REMOVED;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/13/2017.
 */

public class ImageMessageChatTribuUserLeftVH extends RecyclerView.ViewHolder{

    public Context mContext;

    @BindView(R.id.relative_image)
    public RelativeLayout mRelativeImage;

    @BindView(R.id.relative_simple_drawee_view_user_left)
    public RelativeLayout mRelativeSimpleDraweeUserLeft;

    @BindView(R.id.image_frame_user_left)
    public SimpleDraweeView mSdImageFrameUserLeft;

    @BindView(R.id.loading_painel_image_user_left)
    public RelativeLayout mRelativeLoadingPanelImageUserLeft;

    @BindView(R.id.progress_image_user_letf)
    public ProgressBar mProgressImageUserLeft;

    @BindView(R.id.btn_download_image_user_left)
    public ImageButton mBtnDownloadImageUserLeft;

    @BindView(R.id.linear_image_info_user_left)
    public LinearLayout mLinearImageInfoUserLeft;

    @BindView(R.id.relative_image_description_user_left)
    public RelativeLayout mRelativeImageDescriptionUserLeft;

    @BindView(R.id.tv_image_description_user_left)
    public TextView mTvImageDescriptionUserLeft;

    @BindView(R.id.message_time_image_user_left)
    public TextView mTvMessageTimeImageUserLeft;

    @BindView(R.id.iv_garbage_message)
    ImageView mIvGarbageMessage;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference mStorageTribusImagesMessages;
    private DatabaseReference mRefTribusMessage;


    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mTribusCollection;

    //SHOW PROGRESS
    private ProgressDialog progress;



    public ImageMessageChatTribuUserLeftVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);


        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageTribusImagesMessages = mStorage.getReference().child(TRIBUS_IMAGES_MESSAGES);
        mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);


    }

    // IMAGE MESSAGE
    public void setTvImageDescriptionLeft(String description) {
        mTvImageDescriptionUserLeft.setText(description);

    }

    private void setTvMessageTimeImageUserLeft(Date date) {

        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeImageUserLeft.setText(time);
    }

    public void initImageMessageChatTribuUserLeftVH(MessageUser message, ChatTribuView mView, String mTribusKey,
                                                    Uri mImageUri, String mImagePath, Boolean mIsPublic) {

        mIvGarbageMessage.setOnClickListener(v -> {
            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                showDialogToDeleteMessage(mContext, message, mView, mTribusKey);
            }
        });

        //SETUP VISIBILITIES
        //viewHolder.mIncludeLayoutImageMessageUserLeft.setVisibility(VISIBLE);

        //SETUP VIEW
        mBtnDownloadImageUserLeft.setVisibility(GONE);

        if (message.getImage().getDescription() != null) {
            mTvImageDescriptionUserLeft.setVisibility(VISIBLE);
            setTvImageDescriptionLeft(message.getImage().getDescription());
        } else {
            mTvImageDescriptionUserLeft.setVisibility(GONE);

        }

        setTvMessageTimeImageUserLeft(message.getDate());

        if (message.getImage().isUploaded()) {
            downloadImage(message, mTribusKey);


        } else {
            mProgressImageUserLeft.setVisibility(VISIBLE);
            uploadImageToFirebase(mImageUri, mImagePath, message.getKey(), message, mTribusKey);
        }


        mRelativeImage.setVisibility(VISIBLE);
    }

    //DELETE CURRENT USER'S MESSAGE
    private void showDialogToDeleteMessage(Context context, MessageUser message, ChatTribuView mView, String mTribusKey) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remover esta mensagem?");
        builder.setMessage("Mensagem com imagem");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(mContext);
            } else {
                ShowSnackBarInfoInternet.showSnack(true, mView);
                dialog.dismiss();
                showProgressDialog(true, "Aguarde enquanto a mensagem é removida... ");
                deleteImage(context, message, mTribusKey);
            }

        });

        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteImage(Context context, MessageUser message, String mTribusKey) {

        StorageReference fileRef = mStorage.getReferenceFromUrl(message.getImage().getDownloadUri());
        fileRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully

            Map<String, Object> updateTypeMessageToRemoved = new HashMap<>();
            updateTypeMessageToRemoved.put("contentType", REMOVED);

            mTribusCollection
                    .document(mTribusKey)
                    .collection(TOPICS)
                    .document(message.getTopicKey())
                    .collection(TOPIC_MESSAGES)
                    .document(message.getKey())
                    .update(updateTypeMessageToRemoved)
                    .addOnSuccessListener(aVoid2 -> {
                        showProgressDialog(false, null);
                        /*Toast.makeText(context, "A mensagem foi removida. Atualize a tela, arrastando-a de cima para baixo, caso pareça ter sido removida mais de uma mensagem.",
                                Toast.LENGTH_LONG).show();*/
                    })
                    .addOnFailureListener(Throwable::getLocalizedMessage);

            /*mRefTribusMessage
                    .child(mTribusUniqueName)
                    .child(message.getTopicKey())
                    .child(message.getKey())
                    .updateChildren()*/

        }).addOnFailureListener(e -> {
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


    //UPLOAD IMAGES TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadImageToFirebase(Uri mImageUri, String mImagePath, String messageRef, MessageUser message, String mTribusKey) {

        DatabaseReference refMessage = mRefTribusMessage
                .child(mTribusKey)
                .child(message.getTopicKey())
                .child(messageRef);

        final StorageReference filePath = mStorageTribusImagesMessages
                .child(mTribusKey)
                .child(message.getTopicKey())
                .child(mAuth.getCurrentUser().getUid())
                .child("image")
                .child(mImagePath + "/image_message_left.mp4");

        UploadTask task = filePath.putFile(mImageUri);


        Task<Uri> urlTask = task.continueWithTask(task1 -> {
            if (!task1.isSuccessful()) {
                throw task1.getException();
            }

            // Continue with the task to get the download URL
            return filePath.getDownloadUrl();
        });

        urlTask.addOnCompleteListener(task12 -> {
            if (task12.isSuccessful()) {
                String downloadUri = task12.getResult().toString();
                //String uri = filePath.getDownloadUrl().toString();
                Map<String, Object> updateMessage = new HashMap<>();
                updateMessage.put("uploaded", true);
                updateMessage.put("downloadUri", downloadUri);

                refMessage.child("image")
                        .updateChildren(updateMessage)
                        .addOnSuccessListener(aVoid -> {
                            downloadImage(message, mTribusKey);
                        })
                        .addOnFailureListener(Throwable::getLocalizedMessage);


            } else {
                // Handle failures
                task.getException().getMessage();
                Toast.makeText(mContext, "Erro ao enviar imagem para Firebase: put file", Toast.LENGTH_LONG).show();
                refMessage.removeValue();

            }
        });




                //OLD CODE
                /*task.addOnSuccessListener(taskSnapshot -> {
                    String uri = filePath.getDownloadUrl().toString();
                    Map<String, Object> updateMessage = new HashMap<>();
                    updateMessage.put("uploaded", true);
                    updateMessage.put("downloadUri", uri);

                    refMessage.child("image")
                            .updateChildren(updateMessage)
                            .addOnSuccessListener(aVoid -> {
                                downloadImage(message, mTribusUniqueName);
                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);

                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(mContext, "Erro ao enviar imagem para Firebase: put file", Toast.LENGTH_LONG).show();
                    refMessage.removeValue();

                })
                .addOnProgressListener(taskSnapshot -> {
                    //double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();;
                    //System.out.println("Envio " + progress + "% concluído");
                    int currentprogress = (int) progress;
                    //progressBar.setProgress(currentprogress);
                    mProgressImageUserLeft.setProgress(currentprogress);


                });*/


    }

    private void downloadImage(MessageUser message, String mTribusKey) {

        if (message.getImage().getDownloadUri() != null) {
            Uri imageUrl = Uri.parse(message.getImage().getDownloadUri());
            setImageMessage(imageUrl);
            mSdImageFrameUserLeft.setOnClickListener(v -> {
                openShowImageActivity(imageUrl, message.getKey(), mTribusKey);

            });
        }
    }

    //SET IMAGE
    private void setImageMessage(Uri imageUri) {
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
                .setOldController(mSdImageFrameUserLeft.getController())
                .build();
        mSdImageFrameUserLeft.setController(dc);
        mProgressImageUserLeft.setVisibility(GONE);

        //layout message visibility

    }

    private void openShowImageActivity(Uri uri, String ref, String mTribusKey) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowImageActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }

}
