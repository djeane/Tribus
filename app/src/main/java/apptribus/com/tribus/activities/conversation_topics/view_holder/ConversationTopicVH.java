package apptribus.com.tribus.activities.conversation_topics.view_holder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsView;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.*;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 12/26/2017.
 */

public class ConversationTopicVH extends RecyclerView.ViewHolder {

    private Context mContext;

    @BindView(R.id.relative_content)
    RelativeLayout mRelativeContent;

    @BindView(R.id.circle_image_of_participant)
    SimpleDraweeView mCircleImagePartipant;

    @BindView(R.id.textViewOptions)
    TextView mTvOptions;

    @BindView(R.id.view1)
    View mView1;

    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.tv_topic)
    TextView mTvTopic;

    @BindView(R.id.topic_time)
    TextView mTvTopicTime;

    //INSTANCES - FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    private long numMessages = 0;

    //REFERENCES - FIREBASE
    private DatabaseReference mReferenceTribu;
    public DatabaseReference mReferenceUser;
    private DatabaseReference mReferenceTribusFollowers;
    private DatabaseReference mReferenceFollowers;
    private DatabaseReference mRefTribusTopics;
    private DatabaseReference mRefTribusMessages;
    private ValueEventListener mValueListenerRefTribusMessages;
    private ValueEventListener mValueListenerRefUser;

    private ProgressDialog progress;



    public ConversationTopicVH(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
        mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
        mReferenceTribusFollowers = mDatabase.getReference().child(TRIBUS_FOLLOWERS);
        mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
        mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
        mRefTribusMessages = mDatabase.getReference().child(TRIBUS_MESSAGES);

        progress = new ProgressDialog(itemView.getContext());
    }

    private void setTvTopic(String topic, long numTopics){

        int index = topic.length();

        SpannableString styledTopic = new SpannableString(topic);
        styledTopic.setSpan(
                new UnderlineSpan(),
                0,
                 index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        String appendTopic = styledTopic + " (" + numTopics + ")";
        mTvTopic.setText(appendTopic);

    }

    private void setName(String username, String name) {
        if (username != null || name != null) {
            String[] firstName = name.split(" ");
            String appendNameAndUsername = "por " + firstName[0] + " (" + username + ")";
            mTvName.setText(appendNameAndUsername);
        }
    }

    private void setTvTopicTime(Date date) {
        if (date != null) {
            String time = GetTimeAgo.getTimeAgo(date, mContext);
            //String appendDate = "tópico criado " + time;
            mTvTopicTime.setText(time);
        }
    }


    //SET IMAGE OF TALKER
    private void setImageParticipant(String url) {

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
                .setOldController(mCircleImagePartipant.getController())
                .build();
        mCircleImagePartipant.setController(dc);

    }

    public void initViewHolderConversationTopic(Tribu tribu, ConversationTopic topic,
                                                ValueEventListener valueListenerRefUser,
                                                ValueEventListener valueListenerRefTribusMessages,
                                                ConversationTopicsView view){


        mValueListenerRefUser = valueListenerRefUser;
        mValueListenerRefTribusMessages = valueListenerRefTribusMessages;

                        mRefTribusMessages
                                .child(tribu.getProfile().getUniqueName())
                                .child(topic.getKey())
                                .addValueEventListener(mValueListenerRefTribusMessages = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChildren()){
                                            numMessages = dataSnapshot.getChildrenCount();
                                        }

                                        if (topic.getIdParticipant() != null){
                                            mReferenceUser
                                                    .child(topic.getIdParticipant())
                                                    .addValueEventListener(mValueListenerRefUser = new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshotUser) {

                                                                User user = dataSnapshotUser.getValue(User.class);


                                                                mView1.setVisibility(VISIBLE);
                                                                setName(user.getUsername(), user.getName());
                                                                setTvTopic(topic.getTopic(), numMessages);
                                                                setTvTopicTime(topic.getDate());
                                                                mCircleImagePartipant.bringToFront();
                                                                mRelativeContent.invalidate();
                                                                setImageParticipant(user.getThumb());

                                                                if (mAuth.getCurrentUser().getUid().equals(topic.getIdParticipant())){
                                                                    mTvOptions.setVisibility(VISIBLE);

                                                                    mTvOptions.setOnClickListener(v ->
                                                                            setTvOptionsMenu(topic, tribu, view)
                                                                    );
                                                                }

                                                            itemView
                                                                    .setOnClickListener(v -> {

                                                                        FirebaseMessaging.getInstance().subscribeToTopic(topic.getKey());

                                                                        openChatTribuActivity(topic, tribu, view);

                                                                    });

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            databaseError.toException().printStackTrace();
                                                        }
                                                    });


                                        }
                                        else {

                                            mView1.setVisibility(GONE);
                                            setTvTopic(topic.getTopic(), numMessages);
                                            itemView
                                                    .setOnClickListener(v -> {

                                                        FirebaseMessaging.getInstance().subscribeToTopic(topic.getKey());

                                                        openChatTribuActivity(topic, tribu, view);

                                                    });


                                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }

    private void setTvOptionsMenu(ConversationTopic topic, Tribu tribu, ConversationTopicsView view) {

        PopupMenu popup = new PopupMenu(mContext, mTvOptions);
        //inflating menu from xml resource
        popup.inflate(R.menu.menu_poput_topic_options);
        //adding click listener
        popup.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.action_topic_edit:

                    openDialogTopic(tribu, topic, view);

                    return true;
                case R.id.action_close_topic:
                    //handle menu2 click
                    return true;
                default:
                    return false;
            }
        });
        //displaying the popup
        popup.show();

    }

    private void openDialogTopic(Tribu tribu, ConversationTopic topic, ConversationTopicsView view) {

        String oldTopic = topic.getTopic();

        if(tribu != null) {
            //CONFIGURATION OF DIALOG
            AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogTheme);
            LayoutInflater inflater = view.mContext.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_edit_topic, null);

            EditText mEtTopic = dialogView.findViewById(R.id.et_topic);
            mEtTopic.setText(topic.getTopic().trim());
            TextView mTvCharacterCount = dialogView.findViewById(R.id.tv_character_count);
            Button mBtnCancel = dialogView.findViewById(R.id.btn_cancel);
            Button mBtnEditTopic = dialogView.findViewById(R.id.btn_edit_topic);

            builder.setView(dialogView);

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

            mEtTopic.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String appendCount = String.valueOf(s.length()) + "/50";
                    mTvCharacterCount.setText(appendCount);

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            //CLICK LISTENER TO BUTTONS
            mBtnEditTopic.setOnClickListener(v -> {
                //CHECK INTERNET CONNECTION
                if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                    ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                } else {
                    ShowSnackBarInfoInternet.showSnack(true, view);

                    String newTopic = mEtTopic.getText().toString().trim();

                    if (newTopic.equals("")){
                        Toast.makeText(view.mContext, "Por favor, informe o assunto do tópico.", Toast.LENGTH_LONG).show();
                    }
                    else if(oldTopic.equals(newTopic)){
                        Toast.makeText(view.mContext, "Nenhuma alteração foi realizada.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        //ConversationTopic conversationTopic = new ConversationTopic();
                        //conversationTopic.setTopic(newTopic);

                        sendTopicToFirebase(tribu, newTopic, view.mContext, topic);
                    }

                    dialog.dismiss();

                }
            });

            mBtnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });

        }
    }

    private void sendTopicToFirebase(Tribu tribu, String newTopic, AppCompatActivity activity, ConversationTopic oldTopic) {

            progress = new ProgressDialog(activity);
            progress.setCancelable(false);
            progress.setMessage("Atualizando edição...");

            showProgressDialog(true);

            Date date = new Date(System.currentTimeMillis());

            //FirebaseMessaging.getInstance().subscribeToTopic(topicKey);

            Map<String, Object> updateTopic = new HashMap<>();
            updateTopic.put("topic", newTopic);
            updateTopic.put("date", date);
            updateTopic.put("isEdited", true);


            //STORE MESSAGE INSIDE TRIBU'S DATABASE
            mRefTribusTopics
                    .child(tribu.getProfile().getUniqueName())
                    .child(oldTopic.getKey())
                    .updateChildren(updateTopic)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(activity, "Tópico atualizado!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        showProgressDialog(false);
                        e.getLocalizedMessage();
                    });


    }

    //SHOW PROGRESS
    private void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private void openChatTribuActivity(ConversationTopic topic, Tribu tribu,
                                              ConversationTopicsView view) {

        Intent intent = new Intent(mContext, ChatTribuActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TOPIC_KEY, topic.getKey());

        if(view.mLink != null){
            intent.putExtra(LINK_INTO_MESSAGE, view.mLink);
        }

        mContext.startActivity(intent);
    }

}
