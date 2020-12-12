package apptribus.com.tribus.activities.chat_tribu.view_holder.user_right;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import butterknife.BindView;
import butterknife.ButterKnife;

import static apptribus.com.tribus.util.Constantes.FROM_CHAT_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.USER_ID;

/**
 * Created by User on 12/13/2017.
 */

public class RemovedMessageChatTribuUserRightVH extends RecyclerView.ViewHolder {

    public Context mContext;

    @BindView(R.id.row_removed_message_user_right_tribu)
    RelativeLayout mLayoutRemovedTextMessageUserRightTribu;

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

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceUser;

    //FIRESTORE INSTANCE
    private FirebaseFirestore mFirestore;

    //FIRESTORE REFERENCES
    private CollectionReference mUsersCollections;


    public RemovedMessageChatTribuUserRightVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mUsersCollections = mFirestore.collection(GENERAL_USERS);
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mLayoutRemovedTextMessageUserRightTribu.setVisibility(View.VISIBLE);
    }

    private void setUserNameUserRight(String username, String name) {
        String[] firstName = name.split(" ");
        String appendNameAndUsername = firstName[0] + " (" + username + ")";
        mTvUserName.setText(appendNameAndUsername);
    }

    //set image
    private void setImageUserRight(String url) {

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

    //set time text removed message
    private void setTvMessageTimeUserRight(Date date) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault());
        String time = sfd.format(date.getTime());

        mTvMessageTimeUserRight.setText(time);
    }


    public void initRemovedMessageChatTribuUserRightVH(MessageUser message,
                                                       ChatTribuView mView, String mTribusKey, Boolean mIsPublic) {

        mCircleImageUserRight.bringToFront();
        mRelativeTextMessage.invalidate();

        mUsersCollections
                .document(message.getUidUser())
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        User mUserRight = documentSnapshot.toObject(User.class);


                        if (mUserRight.getThumb() != null) {
                            setImageUserRight(mUserRight.getThumb());
                        } else {
                            setImageUserRight(mUserRight.getImageUrl());
                        }

                        setTvMessageTimeUserRight(message.getDate());
                        setUserNameUserRight(mUserRight.getUsername(), mUserRight.getName());

                        //LISTENER FOR BtnFOLLOW
                        mCircleImageUserRight.setOnClickListener(v -> {

                            openDetailActivity(mView, mUserRight.getId(), mAuth.getCurrentUser().getUid(), mTribusKey);

                        });
                    }

                    mLayoutRemovedTextMessageUserRightTribu.setVisibility(View.VISIBLE);

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(mView.mContext.getApplicationContext(), "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                });


    }

    private void openDetailActivity(ChatTribuView view, String talkerId, String userId, String mTribusKey){

        Intent intent = new Intent(view.mContext, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, talkerId);
        intent.putExtra(FROM_CHAT_TRIBUS, FROM_CHAT_TRIBUS);
        intent.putExtra(TRIBU_KEY, mTribusKey);
        intent.putExtra(USER_ID, userId);
        view.mContext.startActivity(intent);
    }


}
