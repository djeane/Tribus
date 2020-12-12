package apptribus.com.tribus.activities.chat_tribu.view_holder.user_right;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.show_image.ShowImageActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.FROM_CHAT_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

/**
 * Created by User on 12/13/2017.
 */

public class ImageMessageChatTribuUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    //image message
    //@BindView(R.id.include_layout_image_message_user_right_tribu)
    //public View mIncludeLayoutImageMessageUserRight;

    @BindView(R.id.layout_image_user_right)
    RelativeLayout mLayoutImageUserRight;

    @BindView(R.id.relative_image)
    RelativeLayout mRelativeImage;

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

    @BindView(R.id.tv_image_description_user_right)
    public TextView mTvImageDescriptionUserRight;

    @BindView(R.id.message_time_image_user_right)
    public TextView mTvMessageTimeImageUserRight;

    @BindView(R.id.tv_username_image)
    public TextView mTvUserNameImage;


    //FIREBASE
    private FirebaseAuth mAuth;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mUsersCollections;


    public ImageMessageChatTribuUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollections = mFirestore.collection(GENERAL_USERS);

    }


    //IMAGE MESSAGE
    //set image
    private void setImageUserRightImageMessage(String url) {

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
                //Log.d("Valor: ", "onIntermediateImageFailed - id: " + id + "throwable: " + throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                //Log.d("Valor: ", "onFinalImageSet - id: " + id + "imageInfo: " + imageInfo + "animatable: " + animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
                //Log.d("Valor: ", "onIntermediateImageSet - id: " + id + "imageInfo: " + imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
                //Log.d("Valor: ", "onRelease - id: " + id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);

                //Log.d("Valor: ", "onSubmit - id: " + id + "callerContext: " + callerContext);
            }
        };

        //SCRIPT - LARGURA DA IMAGEM
        //int w = 0;
        /*if (holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.MATCH_PARENT
                || holder.mImageTribu.getLayoutParams().width == FrameLayout.LayoutParams.WRAP_CONTENT) {

            Display display = ((MainActivity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            try {
                w = size.x;
                Log.d("Valor: ", "Valor da largura(w) em onStart(FragmentPesquisarTribu): " + w);

            } catch (Exception e) {
                w = display.getWidth();
                e.printStackTrace();
            }
        }*/

        Uri uri = Uri.parse(url);
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(listener)
                .setOldController(mCircleImageUserRightImageMessage.getController())
                .build();
        mCircleImageUserRightImageMessage.setController(dc);

    }

    private void setTvImageDescriptionRight(String description) {
        mTvImageDescriptionUserRight.setText(description);
    }

    private void setTvMessageTimeImageUserRight(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeImageUserRight.setText(time);
    }

    private void setUserNameUserRightImage(String username, String name) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";
        mTvUserNameImage.setText(appendNameAndUsername);
    }

    public void initImageMessageChatTribuUserRightVH(MessageUser message,
                                                     ChatTribuView mView, String mTribusKey, Boolean mIsPublic) {

        mCircleImageUserRightImageMessage.bringToFront();
        mRelativeImage.invalidate();


        mUsersCollections
                .document(message.getUidUser())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (message.getImage().getDownloadUri() != null) {

                            User mUserRight = documentSnapshot.toObject(User.class);

                            //SETUPS
                            setUserNameUserRightImage(mUserRight.getUsername(), mUserRight.getName());

                            if (mUserRight.getThumb() != null) {
                                setImageUserRightImageMessage(mUserRight.getThumb());
                            } else {
                                setImageUserRightImageMessage(mUserRight.getImageUrl());
                            }

                            mBtnDownloadImageUserRight.setVisibility(GONE);


                            mRelativeLoadingPanelImageUserRight.setVisibility(GONE);
                            mProgressImageUserRight.setVisibility(GONE);

                            setTvMessageTimeImageUserRight(message.getDate());

                            if (message.getImage().getDescription() != null) {
                                mTvImageDescriptionUserRight.setVisibility(VISIBLE);
                                setTvImageDescriptionRight(message.getImage().getDescription());
                            } else {
                                mTvImageDescriptionUserRight.setVisibility(GONE);

                            }


                            Uri imageUri = Uri.parse(message.getImage().getDownloadUri());

                            setImageMessageUserRight(imageUri);

                            mSdImageFrameUserRight.setOnClickListener(v -> {
                                openShowImageActivity(imageUri, message.getKey(), mTribusKey);
                            });

                            mCircleImageUserRightImageMessage.setOnClickListener(v -> {

                                openDetailActivity(mView, mUserRight.getId(), mAuth.getCurrentUser().getUid(), mTribusKey);

                            });

                        }
                    }
                    mLayoutImageUserRight.setVisibility(VISIBLE);

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(mView.mContext.getApplicationContext(), "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                });

    }

    //USER RIGHT
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

    private void openDetailActivity(ChatTribuView view, String talkerId, String userId, String mTribusKey){

        Intent intent = new Intent(view.mContext, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra(FROM_CHAT_TRIBUS, FROM_CHAT_TRIBUS);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(USER_ID, userId);
        view.mContext.startActivity(intent);
    }


    private void openShowImageActivity(Uri uri, String ref, String mTribusKey) {
        Intent intent = new Intent(mContext.getApplicationContext(), ShowImageActivity.class);
        intent.setData(uri);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(MESSAGE_REFERENCE, ref);
        mContext.startActivity(intent);
    }


    //ADD USER AS TALKER
    //SHOW DIALOG
    /*private void showDialog(Context context, MessageUser message, String mTribusUniqueName) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(context);
        progress.setCancelable(false);

        mReferenceUser
                .child(message.getUidUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User userRight = dataSnapshot.getValue(User.class);

                        //CONFIGURATION OF DIALOG
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Adicionar contato");
                        builder.setMessage("Conversar com " + userRight.getName() +
                                "(" + userRight.getUsername() + ")?");

                        String positiveText = "SIM";
                        builder.setPositiveButton(positiveText, (dialog, which) -> {
                            dialog.dismiss();
                            showProgressDialog(true, "Aguarde enquanto o contato é adicionado...");
                            createTalk(context, userRight, mTribusUniqueName);

                        });

                        String negativeText = "NÃO";
                        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
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

    //CREATE TALKS
    private void createTalk(Context context, User userInvited, String mTribusUniqueName) {

        mReferenceTribu
                .child(mTribusUniqueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Tribu tribu = dataSnapshot.getValue(Tribu.class);

                        Date date = new Date(System.currentTimeMillis());

                        //USER RIGHT
                        Talk talkerUserRight = new Talk(tribu.getProfile().getNameTribu(), tribu.getProfile().getUniqueName(),
                                userInvited.getId());
                        talkerUserRight.setDateInvitation(date);

                        //USER LEFT
                        Talk talkerUserLeft = new Talk(tribu.getProfile().getNameTribu(), tribu.getProfile().getUniqueName(),
                                mAuth.getCurrentUser().getUid());
                        talkerUserLeft.setDateInvitation(date);

                        if (userInvited.isAccepted()) {
                            talkerUserRight.setFromPermission(false);
                            talkerUserLeft.setFromPermission(false);
                            //STORE TALK RIGHT INSIDE TALK LEFT
                            mRefUsersTalk
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(userInvited.getId())
                                    .setValue(talkerUserRight)
                                    .addOnSuccessListener(task -> {

                                        //STORE TALK LEFT INSIDE TALK RIGHT
                                        mRefUsersTalk
                                                .child(userInvited.getId())
                                                .child(mAuth.getCurrentUser().getUid())
                                                .setValue(talkerUserLeft)
                                                .addOnSuccessListener(task1 -> {

                                                    //ADD TALKER ADDED INSIDE THIS NODE(TALKER ADDED)
                                                    mRefUsersTalkAdded
                                                            .child(mAuth.getCurrentUser().getUid())
                                                            .child(userInvited.getId())
                                                            .setValue(talkerUserLeft)
                                                            .addOnSuccessListener(task2 -> {
                                                                //SHOW
                                                                showProgressDialog(false, null);
                                                                Toast.makeText(context, "Contato adicionado!", Toast.LENGTH_SHORT).show();
                                                                mCircleImageUserRightImageMessage.setEnabled(false);
                                                            })
                                                            .addOnFailureListener(Throwable::getLocalizedMessage);

                                                })
                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                    })
                                    .addOnFailureListener(Throwable::getLocalizedMessage);

                        } else {

                            //TALKER RIGHT - TO SEE HIS TALKS WHICH ARE WAITING PERMISSION
                            mRefUsersTalkersPermissions
                                    .child(userInvited.getId())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .setValue(talkerUserLeft)
                                    .addOnSuccessListener(taskUserLeft -> {

                                        //USER LEFT - TO SEE HIS INVITATIONS
                                        mRefUsersTalkersInvitations
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child(userInvited.getId())
                                                .setValue(talkerUserRight)
                                                .addOnSuccessListener(taskUserRight -> {

                                                    //SHOW
                                                    showProgressDialog(false, null);
                                                    Toast.makeText(context, "Convite enviado!", Toast.LENGTH_SHORT).show();
                                                    mCircleImageUserRightImageMessage.setEnabled(false);
                                                })
                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                    })
                                    .addOnFailureListener(Throwable::getLocalizedMessage);


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }
                });


    }

    private void openChatTalker(String talkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra("fromChatTribu", "fromChatTribu");
        mContext.startActivity(intent);
    }

    private void showToastIfWaitingPermission(String userRightId) {

        mReferenceUser
                .child(userRightId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);

                        String message = user.getName() + " ainda não aceitou seu convite para " +
                                "conversar de modo restrito.";

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
    }*/
}
