package apptribus.com.tribus.activities.chat_tribu.view_holder.user_right;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.pojo.CollectionTag;
import apptribus.com.tribus.pojo.MessageTag;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.UserUpdate;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter.mListener;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TAGS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.MESSAGE_TAGS;
import static apptribus.com.tribus.util.Constantes.TAG_COLLECTION;
import static apptribus.com.tribus.util.Constantes.TAG_UPDATE;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.USERS_UPDATES;
import static com.mikepenz.iconics.Iconics.TAG;

/**
 * Created by User on 12/13/2017.
 */

public class TextMessageChatTribuUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.row_text_message_user_right_tribu)
    RelativeLayout mLayoutRowTextMessageUserRightTribu;

    @BindView(R.id.relative_text_message)
    RelativeLayout mRelativeTextMessage;

    @BindView(R.id.tv_message_user_right)
    public TextView mTvMessageUserRight;

    @BindView(R.id.circle_user_image_right)
    public SimpleDraweeView mCircleImageUserRight;

    @BindView(R.id.message_time_user_right)
    public TextView mTvMessageTimeUserRight;

    @BindView(R.id.tv_username)
    TextView mTvUserName;

    @BindView(R.id.iv_reply)
    ImageView mIvReply;

    @BindView(R.id.sv_tag)
    SimpleDraweeView mSvTag;

    @BindView(R.id.sv_tag_replace)
    SimpleDraweeView mSvTagReplace;

    @BindView(R.id.tv_tag_num)
    TextView mTvTagNum;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mUsersCollection;
    private CollectionReference mTribusCollection;
    private CollectionReference mTagCollection;


    //SHOW PROGRESS
    private ProgressDialog progress;

    public TextMessageChatTribuUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollection = mFirestore.collection(GENERAL_USERS);
        mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
        mTagCollection = mFirestore.collection(GENERAL_TAGS);

        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);

        mCircleImageUserRight.bringToFront();
        mRelativeTextMessage.invalidate();

    }


    //TEXT MESSAGE
    //set message
    public void setTvMessageUserRight(String message) {
        mTvMessageUserRight.setText(message);
    }

    public void setTvMessageLinkUserRight(SpannableString message) {
        mTvMessageUserRight.setText(message);
    }


    public void setUserNameUserRight(String username, String name) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";
        mTvUserName.setText(appendNameAndUsername);
    }

    //set image
    public void setImageUserRight(String url) {

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
                .setOldController(mCircleImageUserRight.getController())
                .build();
        mCircleImageUserRight.setController(dc);

    }

    //set image tag
    private void setImageTag(String url) {

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

        //if (url != null) {
            Uri uri = Uri.parse(url);
            DraweeController dc = Fresco.newDraweeControllerBuilder()
                    .setUri(uri)
                    .setControllerListener(listener)
                    .setOldController(mSvTag.getController())
                    .build();
            mSvTag.setController(dc);
            mSvTag.setVisibility(VISIBLE);
            mSvTagReplace.setVisibility(INVISIBLE);

        //}
        //else {
          //  mSvTag.setImageResource(R.drawable.ic_action_placeholder_user_accent);
        //}

    }

    //set time text message
    public void setTvMessageTimeUserRight(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeUserRight.setText(time);
    }


    public void initTextMessageChatTribuUserRightVH(MessageUser message,
                                                    ChatTribuView mView, String tribuKey, Boolean mIsPublic) {


        if (mIsPublic) {
            mSvTag.setVisibility(VISIBLE);
            mTvTagNum.setVisibility(VISIBLE);
        } else {
            mSvTag.setVisibility(INVISIBLE);
            //mTvTagNum.setVisibility(GONE);
        }


        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .collection(MESSAGE_TAGS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .addSnapshotListener(mView.mContext, (queryDocumentSnapshots, e) -> {

                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        setTagNum(queryDocumentSnapshots.getDocumentChanges().size());
                    }
                    else if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()){
                        setTagNum(0);
                    }

                });

        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .collection(MESSAGE_TAGS)
                .orderBy(DATE, Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(mView.mContext, (queryDocumentSnapshots, e) -> {

                    if (e != null){
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                        DocumentChange dc = queryDocumentSnapshots.getDocumentChanges().get(0);

                        MessageTag tag = dc.getDocument().toObject(MessageTag.class);

                        mUsersCollection
                                .document(tag.getCurrentUserId())
                                .addSnapshotListener(mView.mContext, (documentSnapshot, e1) -> {

                                    if (e1 != null){
                                        return;
                                    }

                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        User userTag = documentSnapshot.toObject(User.class);

                                        if (userTag.getThumb() != null) {
                                            setImageTag(userTag.getThumb());
                                        } else {
                                            setImageTag(userTag.getImageUrl());

                                        }
                                    }

                                });

                    }
                    else {
                        mSvTagReplace.setVisibility(VISIBLE);
                        mSvTag.setVisibility(INVISIBLE);

                    }
                });



        mUsersCollection
                .document(message.getUidUser())
                .addSnapshotListener(mView.mContext, (documentSnapshot, e) -> {

                    if (e != null){

                        return;
                    }

                    mTribusCollection
                            .document(tribuKey)
                            .addSnapshotListener(mView.mContext, (documentSnapshotTribu, eTribu) -> {

                                if (eTribu != null){
                                    return;
                                }

                                if (documentSnapshotTribu != null && documentSnapshotTribu.exists()){
                                    Tribu tribu = documentSnapshotTribu.toObject(Tribu.class);

                                    mSvTag.setOnClickListener(v -> {
                                        showDialogToTag(message, tribuKey, tribu);
                                    });

                                    mSvTagReplace.setOnClickListener(v -> {
                                        showDialogToTag(message, tribuKey, tribu);
                                    });
                                }

                                if (documentSnapshot != null && documentSnapshot.exists()){
                                    User mUserRight = documentSnapshot.toObject(User.class);

                                    if (mUserRight.getThumb() != null) {
                                        setImageUserRight(mUserRight.getThumb());
                                    } else {
                                        setImageUserRight(mUserRight.getImageUrl());
                                    }


                                    setTvMessageUserRight(message.getMessage());

                                    setTvMessageTimeUserRight(message.getDate());
                                    setUserNameUserRight(mUserRight.getUsername(), mUserRight.getName());


                                    //LISTENER FOR BtnFOLLOW
                                    mCircleImageUserRight.setOnClickListener(v -> {
                                        mListener.openDetailUserActivity(mUserRight.getId(), mAuth.getCurrentUser().getUid(), tribuKey);
                                    });

                                    mIvReply.setOnClickListener(v -> {
                                        mListener.onReplyMessageText(message);
                                    });


                                }

                                mLayoutRowTextMessageUserRightTribu.setVisibility(VISIBLE);

                            });


                });

        /*mUsersCollection
                .document(message.getUidUser())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                    }
                })
                .addOnFailureListener(e -> {
                });*/

    }

    private void setTagNum(int number){
        mTvTagNum.setText(String.valueOf(number));
    }

    public interface TextMessageChatTribuUserRightListener {
        void onReplyMessageText(MessageUser message);

        void openDetailUserActivity(String contactId, String userId, String tribuKey);
    }


    private void openChatTalker(String talkerId) {
        Intent intent = new Intent(mContext.getApplicationContext(), ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra("fromChatTribu", "fromChatTribu");
        mContext.startActivity(intent);
    }


    public void initLinkMessageChatTribuUserRightVH(MessageUser message,
                                                    ChatTribuView mView, String mTribusKey, Boolean mIsPublic) {

        mCircleImageUserRight.bringToFront();
        mRelativeTextMessage.invalidate();

        if (mIsPublic) {
            mSvTag.setVisibility(VISIBLE);
            mTvTagNum.setVisibility(VISIBLE);
        } else {
            mSvTag.setVisibility(GONE);
            mTvTagNum.setVisibility(GONE);
        }


        mReferenceUser
                .child(message.getUidUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        User mUserRight = dataSnapshot.getValue(User.class);

                        String link = message.getLink();

                        if (mUserRight.getThumb() != null) {
                            setImageUserRight(mUserRight.getThumb());
                        } else {
                            setImageUserRight(mUserRight.getImageUrl());
                        }

                        //SETUP VIEW
                        setTvMessageUserRight(message.getMessage());
                        //setTvMessageTimeUserRight(message.getTimestampCreatedLong());
                        setUserNameUserRight(mUserRight.getUsername(), mUserRight.getName());

                        setMessageLinkUserRight(link, message);

                        mIvReply.setOnClickListener(v -> {
                            mListener.onReplyMessageText(message);
                        });


                        //LISTENER FOR BtnFOLLOW
                        mCircleImageUserRight.setOnClickListener(v -> {

                            mListener.openDetailUserActivity(mUserRight.getId(), mAuth.getCurrentUser().getUid(), mTribusKey);
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }

    private void setMessageLinkUserRight(String link, MessageUser message) {

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

        setTvMessageLinkUserRight(linkStyled);
        mTvMessageUserRight.setMovementMethod(LinkMovementMethod.getInstance());


    }

    private void showDialogToTag(MessageUser message, String tribuKey, Tribu mTribu) {

        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(mContext);
        progress.setCancelable(false);


        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .collection(MESSAGE_TAGS)
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshotTag -> {

                    if (documentSnapshotTag != null && documentSnapshotTag.exists()){
                        /*mUsersCollection
                                .document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    User currentUser = documentSnapshot.toObject(User.class);*/

                                    //CONFIGURATION OF DIALOG
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    //builder.setTitle("Remover tag");
                                    builder.setMessage("Remover tag deste conteúdo?");

                                    String positiveText = "SIM";
                                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                                        dialog.dismiss();
                                        showProgressDialog(true, "Aguarde...");
                                        removeTag(message, tribuKey, mTribu);

                                    });

                                    String negativeText = "NÃO";
                                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                /*});
                                .addOnFailureListener(Throwable::printStackTrace);*/
                    }
                    else {
                        /*mUsersCollection
                                .document(mAuth.getCurrentUser().getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    User currentUser = documentSnapshot.toObject(User.class);*/

                                    //CONFIGURATION OF DIALOG
                                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                    //builder.setTitle("Tag");
                                    builder.setMessage("Adicionar tag a este conteúdo?");

                                    String positiveText = "SIM";
                                    builder.setPositiveButton(positiveText, (dialog, which) -> {
                                        dialog.dismiss();
                                        showProgressDialog(true, "Aguarde...");
                                        setTag(message, tribuKey, mTribu);

                                    });

                                    String negativeText = "NÃO";
                                    builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                /*})
                                .addOnFailureListener(Throwable::printStackTrace);*/
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);




    }


    private void setTag(MessageUser message, String tribuKey, Tribu tribu) {

        String thematic = tribu.getProfile().getThematic();

        String keyTag = mReferenceUser.push().getKey();

        Date date = new Date(System.currentTimeMillis());

        MessageTag tag = new MessageTag();
        tag.setCurrentUserId(mAuth.getCurrentUser().getUid());
        tag.setMessageKey(message.getKey());
        tag.setDate(date);
        tag.setTagKey(keyTag);

        CollectionTag collectionTag = new CollectionTag();
        collectionTag.setDate(date);
        collectionTag.setTribuKeyTag(tribuKey);
        collectionTag.setKeyTag(keyTag);
        collectionTag.setMessageKeyTag(message.getKey());
        collectionTag.setTopicKeyTag(message.getTopicKey());
        collectionTag.setUserIdTag(mAuth.getCurrentUser().getUid());

        Map<String, Object> updateThematic = new HashMap<>();
        updateThematic.put("thematic", thematic);

        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .collection(MESSAGE_TAGS)
                .document(mAuth.getCurrentUser().getUid())
                .set(tag)
                .addOnSuccessListener(aVoid -> {

                    mTagCollection
                            .document(thematic)
                            .set(updateThematic)
                            .addOnSuccessListener(aVoid12 -> {

                                mTagCollection
                                        .document(thematic)
                                        .collection(TAG_COLLECTION)
                                        .document(collectionTag.getKeyTag())
                                        .set(collectionTag)
                                        .addOnSuccessListener(aVoid1 -> {

                                            UserUpdate update = new UserUpdate();
                                            update.setDate(date);
                                            update.setTag(collectionTag);
                                            update.setUpdateType(TAG_UPDATE);
                                            update.setUpdateKey(keyTag);

                                            mUsersCollection
                                                    .document(mAuth.getCurrentUser().getUid())
                                                    .collection(USERS_UPDATES)
                                                    .document(update.getUpdateKey())
                                                    .set(update)
                                                    .addOnSuccessListener(aVoid2 -> {
                                                        showProgressDialog(false, null);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        showProgressDialog(false, null);
                                                    });


                                        })
                                        .addOnFailureListener(e -> {
                                            showProgressDialog(false, null);
                                        });

                            })
                            .addOnFailureListener(e -> {
                                showProgressDialog(false, null);
                            });

                })
                .addOnFailureListener(e -> {
                    showProgressDialog(false, null);
                    e.printStackTrace();
                });

    }


    private void removeTag(MessageUser message, String tribuKey, Tribu tribu) {

        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(message.getTopicKey())
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .collection(MESSAGE_TAGS)
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        MessageTag tag = documentSnapshot.toObject(MessageTag.class);

                        mTribusCollection
                                .document(tribuKey)
                                .collection(TOPICS)
                                .document(message.getTopicKey())
                                .collection(TOPIC_MESSAGES)
                                .document(message.getKey())
                                .collection(MESSAGE_TAGS)
                                .document(mAuth.getCurrentUser().getUid())
                                .delete()
                                .addOnSuccessListener(aVoid -> {

                                    mTagCollection
                                            .document(tribu.getProfile().getThematic())
                                            .collection(TAG_COLLECTION)
                                            .document(tag.getTagKey())
                                            .delete()
                                            .addOnSuccessListener(aVoid1 -> {

                                                mUsersCollection
                                                        .document(mAuth.getCurrentUser().getUid())
                                                        .collection(USERS_UPDATES)
                                                        .document(tag.getTagKey())
                                                        .delete()
                                                        .addOnSuccessListener(aVoid2 -> showProgressDialog(false, null))
                                                        .addOnFailureListener(e -> {
                                                            e.printStackTrace();
                                                            showProgressDialog(false, null);
                                                            Toast.makeText(mContext, "Houve um erro ao remover a tag.", Toast.LENGTH_SHORT).show();
                                                        });


                                                //Toast.makeText(mContext, "Tag removida!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                e.printStackTrace();
                                                showProgressDialog(false, null);
                                                Toast.makeText(mContext, "Houve um erro ao remover a tag.", Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    showProgressDialog(false, null);
                                    Toast.makeText(mContext, "Houve um erro ao remover a tag.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    showProgressDialog(false, null);
                    Toast.makeText(mContext, "Houve um erro ao remover a tag.", Toast.LENGTH_SHORT).show();
                });


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


    }

    //ADD USER AS TALKER
    //SHOW DIALOG
    private void showDialog(Context context, MessageUser message, String mTribusKey) {

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
                            createTalk(context, userRight, mTribusKey);

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
    private void createTalk(Context context, User userInvited, String mTribusKey) {

       /* mReferenceTribu
                .child(mTribusKey)
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
                                                                mCircleImageUserRight.setEnabled(false);
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
                                                    mCircleImageUserRight.setEnabled(false);
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
                });*/


    }

}
