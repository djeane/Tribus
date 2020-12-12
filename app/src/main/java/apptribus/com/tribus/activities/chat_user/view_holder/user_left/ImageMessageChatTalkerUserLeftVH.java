package apptribus.com.tribus.activities.chat_user.view_holder.user_left;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image_talker.ShowImageTalkerActivity;
import apptribus.com.tribus.pojo.ChatTalker;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.TALKERS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 12/14/2017.
 */

public class ImageMessageChatTalkerUserLeftVH extends RecyclerView.ViewHolder {

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
    private DatabaseReference mReferenceUser;
    private DatabaseReference mRefUsersTalk;
    private DatabaseReference mRefTalkersMessage;
    private StorageReference mStorageImagesTalkers;
    private DatabaseReference mRefChatTalker;




    //SHOW PROGRESS
    private ProgressDialog progress;


    public ImageMessageChatTalkerUserLeftVH(View itemView) {
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
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        mRelativeImage.setVisibility(VISIBLE);

    }

    // IMAGE MESSAGE
    public void setTvImageDescriptionLeft(String description) {
        mTvImageDescriptionUserLeft.setText(description);
    }

    public void setTvMessageTimeImageUserLeft(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault());
        String time = sfd.format(new Date(timestamp));

        mTvMessageTimeImageUserLeft.setText(time);
    }


    public void initImageMessageChatTalkerUserLeftVH(MessageUser message, String mTalkerId, String mImagePath, Uri mImageUri) {

        //SETUP VISIBILITIES
        mBtnDownloadImageUserLeft.setVisibility(GONE);

        //SETUP VIEW
        if (message.getImage().getDescription() != null) {
            mTvImageDescriptionUserLeft.setVisibility(VISIBLE);
            setTvImageDescriptionLeft(message.getImage().getDescription());
        } else {
            mTvImageDescriptionUserLeft.setVisibility(GONE);
        }
        //setTvMessageTimeImageUserLeft(message.getTimestampCreatedLong());

        if (message.getImage().isUploaded()) {
            downloadImage(message, mTalkerId);

        } else {
            mProgressImageUserLeft.setVisibility(VISIBLE);
            uploadImageToFirebase(mImagePath, mImageUri, message.getKey(), message, mTalkerId);
        }

    }

    //user left
    public void clickToDeleteImageMessageUserLeft(MessageUser message, String mTalkerId) {
        mIvGarbageMessage.setOnClickListener(v -> {
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


    //SHOW PROGRESS
    private void showProgressDialog(boolean load, String message) {

        if (load) {
            progress.setMessage(message);
            progress.show();
        } else {
            progress.dismiss();
        }
    }



    private void downloadImage(MessageUser message, String mTalkerId) {

        if (message.getImage().getDownloadUri() != null) {
            Uri imageUrl = Uri.parse(message.getImage().getDownloadUri());
            setImageMessage(imageUrl);
            mSdImageFrameUserLeft.setOnClickListener(v -> {
                openShowImageActivity(imageUrl, message.getKey(), mTalkerId);

            });
        }
    }

    private void openShowImageActivity(Uri uri, String ref, String mTalkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowImageTalkerActivity.class);
        intent.setData(uri);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
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

    }


    //UPLOAD IMAGES TO FIREBASE
    @SuppressWarnings("VisibleForTests")
    private void uploadImageToFirebase(String mImagePath, Uri mImageUri, String messageRef, MessageUser message, String mTalkerId) {

        //IMAGE MESSAGE INSIDE CURRENT TALKER
        DatabaseReference refMessage = mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId)
                .child(messageRef);

        //IMAGE MESSAGE INSIDE RIGHT TALKER
        DatabaseReference refMessageRightTalker = mRefTalkersMessage
                .child(mTalkerId)
                .child(mAuth.getCurrentUser().getUid())
                .child(messageRef);

        //DatabaseReference refMessage = mRefTalkersMessage.child(mTalkerId).child(messageRef);

        final StorageReference filePath = mStorageImagesTalkers
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
                                        downloadImage(message, mTalkerId);

                                        mReferenceUser
                                                .child(mTalkerId)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        User talkerUser = dataSnapshot.getValue(User.class);

                                                        if (!talkerUser.isOnlineInChat()) {
                                                            updateNumUnreadMessages(mTalkerId);

                                                            updateChatTalker("Mensagem com Imagem", mTalkerId);
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


            } else {
                // Handle failures
                task12.getException().getMessage();
                Toast.makeText(mContext, "Erro ao enviar imagem.", Toast.LENGTH_LONG).show();
                refMessage.removeValue();

            }
        });



        //OLD CODE
        /*filePath.putFile(mImageUri)
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
                                            downloadImage(message, mTalkerId);

                                            mReferenceUser
                                                    .child(mTalkerId)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            User talkerUser = dataSnapshot.getValue(User.class);

                                                            if (!talkerUser.isOnlineInChat()) {
                                                                updateNumUnreadMessages(mTalkerId);

                                                                updateChatTalker("Mensagem com Imagem", mTalkerId);
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

                    mProgressImageUserLeft.setProgress(currentprogress);

                });*/


    }

    private void updateChatTalker(String message, String mTalkerId) {
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


}
