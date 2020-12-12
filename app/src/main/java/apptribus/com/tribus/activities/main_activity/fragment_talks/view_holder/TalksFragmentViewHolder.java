package apptribus.com.tribus.activities.main_activity.fragment_talks.view_holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.pojo.ChatTalker;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Talk;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 6/13/2017.
 */

public class TalksFragmentViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.circle_image_of_talker)
    public SimpleDraweeView mCircleImageTalker;

    @BindView(R.id.constraint_layout)
    public ConstraintLayout mConstraintLayout;

    @BindView(R.id.tv_name_of_talker)
    TextView mTvNameTalker;

    @BindView(R.id.tv_username)
    TextView mTvUsernameTalker;

    @BindView(R.id.iv_online)
    public ImageView mIvOnline;

    @BindView(R.id.tv_count_badge)
    public TextView mTvCountBadge;

    @BindView(R.id.tv_data)
    public TextView mTvData;

    @BindView(R.id.tv_message)
    public TextView mTvMessage;

    //FIREBASE INSTANCES
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUsers;
    private DatabaseReference mReferenceTalks;
    private DatabaseReference mRefChatTalker;
    private DatabaseReference mRefMessagesTalker;
    private ValueEventListener mValueEventListenerChatTalker;
    private ValueEventListener mValueEventListenerMessages;


    public TalksFragmentViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUsers = mDatabase.getReference().child(GENERAL_USERS);
        mReferenceTalks = mDatabase.getReference().child(USERS_TALKS);
        mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
        mRefMessagesTalker = mDatabase.getReference().child(TALKERS_MESSAGES);

        mCircleImageTalker.bringToFront();
        mConstraintLayout.invalidate();

    }


    //SET IMAGE OF TALKER
    private void setImageOfTalker(String url) {

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
                //.setControllerListener(listener)
                .setOldController(mCircleImageTalker.getController())
                .build();
        mCircleImageTalker.setController(dc);

    }


    //SET NAME OF TRIBU
    /*private void setNameOfTribu(String nameOfTribu) {
        String append = "(da mTribu " + nameOfTribu + ")";
        mTvNameTribu.setText(append);
    }*/


    //SET USERNAME OF TALKER
    private void setTvUsernameTalker(String usernameTalker) {
        mTvUsernameTalker.setText(usernameTalker);
    }


    //SET NAME OF TALKER
    private void setNameOfTalker(String nameTalker) {
        mTvNameTalker.setText(nameTalker);
    }

    private void setTvCountBadge(int countBadge) {
        String countUnreadMessages = String.valueOf(countBadge);
        mTvCountBadge.setText(countUnreadMessages);
    }

    private void setLastMessageDate(Date date) {
            String time = GetTimeAgo.getTimeAgo(date, mContext);
            mTvData.setText(time);

    }

    private void setTvMessage(String message) {
        mTvMessage.setText(message);
    }

    //SET LAST MESSAGE
    private void setTvMessageChildAdded(MessageUser messageUser){

        if (messageUser != null && messageUser.getContentType() != null) {
            switch (messageUser.getContentType()) {
                case "TEXT":
                case "LINK":
                    mTvMessage.setText(messageUser.getMessage());
                    break;
                case "VOICE":
                    String messageAudio = "Mensagem de Audio";
                    mTvMessage.setText(messageAudio);
                    break;
                case "IMAGE":
                    String messageImage = "Mensagem com Imagem";
                    mTvMessage.setText(messageImage);
                    break;
                case "VIDEO": {
                    String messageVideo = "Mensagem de Video";
                    mTvMessage.setText(messageVideo);
                    break;
                }
                default: {
                    String emptyMessage = "Não há mensagem no momento.";
                    mTvMessage.setText(emptyMessage);
                    break;
                }
            }

        }
        else {
            if (mTvMessage != null) {
                String emptyMessage = "Não há mensagem no momento.";
                mTvMessage.setText(emptyMessage);
            }
        }
    }


    public void initTalksFragmentVH(Talk talk, ValueEventListener mValueEventListenerUser,
                                    ValueEventListener valueEventListenerChatTalker,
                                    ValueEventListener valueEventListenerMessages,
                                    Fragment fragmentContext){

        mValueEventListenerChatTalker = valueEventListenerChatTalker;
        mValueEventListenerMessages = valueEventListenerMessages;

        mReferenceUsers
                .child(talk.getTalkerId())
                .addValueEventListener(mValueEventListenerUser = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshotTalker) {

                        mRefChatTalker
                                .child(mAuth.getCurrentUser().getUid())
                                .child(talk.getTalkerId())
                                .addValueEventListener(mValueEventListenerChatTalker = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        User userTalker = dataSnapshotTalker.getValue(User.class);

                                        if (userTalker.getThumb() != null) {
                                            setImageOfTalker(userTalker.getThumb());
                                        }
                                        else {
                                            setImageOfTalker(userTalker.getImageUrl());
                                        }

                                        setNameOfTalker(userTalker.getName());
                                        setTvUsernameTalker(userTalker.getUsername());

                                        if (userTalker.isOnline()) {
                                            //CHECK INTERNET CONNECTION
                                            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                                                mIvOnline.setVisibility(INVISIBLE);
                                            } else {
                                                mIvOnline.setVisibility(VISIBLE);

                                            }
                                        } else {
                                            mIvOnline.setVisibility(INVISIBLE);
                                        }

                                        itemView.setOnClickListener(v -> {
                                            openChatUser(talk);
                                        });

                                        mCircleImageTalker.setOnClickListener(v -> {
                                            openImageTalker(userTalker, fragmentContext);
                                        });

                                        mIvOnline.setOnClickListener(v -> {
                                            showToast(userTalker);
                                        });

                                        mRefMessagesTalker
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child(userTalker.getId())
                                                //.orderByKey()
                                                .orderByChild("timestamp")
                                                .limitToLast(1)
                                                .addValueEventListener(mValueEventListenerMessages = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshotMessages) {


                                                        if (dataSnapshot.hasChildren()) {

                                                            ChatTalker chatTalker = dataSnapshot.getValue(ChatTalker.class);

                                                            if (chatTalker.getUnreadMessages() > 0) {
                                                                setLastMessageDate(chatTalker.getDate());
                                                                mTvData.setVisibility(VISIBLE);
                                                                setTvMessage(chatTalker.getMessage());

                                                                setTvCountBadge(chatTalker.getUnreadMessages());
                                                                mTvCountBadge.setVisibility(VISIBLE);

                                                            }
                                                            else {
                                                                mTvCountBadge.setVisibility(INVISIBLE);
                                                                if (dataSnapshotMessages.hasChildren()) {

                                                                    for (DataSnapshot postSnapshot : dataSnapshotMessages.getChildren()) {

                                                                        MessageUser messageUser = postSnapshot.getValue(MessageUser.class);


                                                                        setTvMessageChildAdded(messageUser);

                                                                        setLastMessageDate(messageUser.getDate());
                                                                        mTvData.setVisibility(VISIBLE);

                                                                    }

                                                                } else {
                                                                    mTvData.setVisibility(INVISIBLE);
                                                                    setTvMessage("Não há mensagem no momento.");


                                                                }
                                                            }

                                                        } else if (dataSnapshotMessages.hasChildren()) {
                                                            for (DataSnapshot postSnapshot : dataSnapshotMessages.getChildren()) {

                                                                MessageUser messageUser = postSnapshot.getValue(MessageUser.class);


                                                                setTvMessageChildAdded(messageUser);

                                                                setLastMessageDate(messageUser.getDate());
                                                                mTvData.setVisibility(VISIBLE);

                                                            }
                                                        } else {
                                                            mTvData.setVisibility(INVISIBLE);
                                                            setTvMessage("Não há mensagem no momento.");
                                                        }


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        databaseError.toException().printStackTrace();

                                                    }
                                                });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        databaseError.toException().printStackTrace();

                                    }
                                });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();

                    }
                });
    }


    private void openChatUser(Talk talker) {
        Intent intent = new Intent(mContext, ChatUserActivity.class);
        intent.putExtra(CONTACT_ID, talker.getTalkerId());
        mContext.startActivity(intent);
    }


    private void openImageTalker(User user, Fragment fragmentContext) {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentContext.getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = fragmentContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_tribu, null);
        SimpleDraweeView mSdImageTalker = dialogView.findViewById(R.id.sd_image_tribu);
        TextView mTvNameOfTribu = dialogView.findViewById(R.id.tv_name_of_tribu);
        TextView mTvUniqueName = dialogView.findViewById(R.id.tv_unique_name);
        TextView mTvAdminSince = dialogView.findViewById(R.id.tv_created_date);


        mTvNameOfTribu.setText(user.getName());
        mTvUniqueName.setText(user.getUsername());
        builder.setView(dialogView);

        /*if (mTribu.getTimestampCreated() != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM HH:mm");
            String time = sfd.format(new Date(mTribu.getTimestampCreatedLong()));

            String appendTime = "Criada em: " + time;
            Log.d("Valor: ", "appendTime: " + appendTime);
            mTvAdminSince.setText(appendTime);

        } else {*/
        String time = GetTimeAgo.getTimeAgo(user.getDate(), fragmentContext.getContext());
        String append = "Perfil criado ";
        String appendDate = append + time;
        mTvAdminSince.setText(appendDate);
        //}

        //Log.d("Valor: ", "image - View: " + admin.getImageUrl());
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
                .setUri(Uri.parse(user.getImageUrl()))
                .setControllerListener(listener)
                .setOldController(mSdImageTalker.getController())
                .build();
        mSdImageTalker.setController(dc);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        dialog.getWindow().setGravity(wmlp.gravity);


        dialog.show();

    }

    private void showToast(User userTalker) {

        String message = userTalker.getName() + " está online";

        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

}
