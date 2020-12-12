package apptribus.com.tribus.activities.conversation_topics.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Date;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.activities.conversation_topics.mvp.ConversationTopicsView;
import apptribus.com.tribus.activities.conversation_topics.view_holder.ConversationTopicVH;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.USERS_MESSAGES;

/**
 * Created by User on 12/26/2017.
 */

public class ConversationTopicAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    public static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mReferenceTribusFollowers = mDatabase.getReference().child(TRIBUS_FOLLOWERS);
    private static DatabaseReference mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
    public static DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
    public static DatabaseReference mRefTribusMessages = mDatabase.getReference().child(TRIBUS_MESSAGES);
    private static DatabaseReference mRefUsersMessages = mDatabase.getReference().child(USERS_MESSAGES);

    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu mTribu;
    private static User mUser;
    private static FirebaseRecyclerAdapter<ConversationTopic, ConversationTopicVH> mAdapter;

    //SHOW
    private static ProgressDialog progress;

    //LISTENERS
    public static ValueEventListener mValueListenerRefUser;
    public static ValueEventListener mValueListenerRefTribusTopic;
    public static ValueEventListener mValueListenerRefTribusMessages;

    //GET USER(CURRENT TALKER)
    public static Observable<User> getUser() {
        String uid = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber ->
                mReferenceUser.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        subscriber.onNext(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        subscriber.onError(databaseError.toException());
                    }
                }));
    }

    public static Observable<Boolean> hasChildren(String mTribusKey) {
        return Observable.create(subscriber -> {
            mRefTribusTopics
                    .child(mTribusKey)
                    .addValueEventListener(mValueListenerRefTribusTopic = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                subscriber.onNext(true);
                            } else {
                                subscriber.onNext(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            subscriber.onError(databaseError.toException());
                        }
                    });
        });

    }


    //GET TRIBU
    public static Observable<Tribu> getTribu(String uniqueName) {

        return Observable.create(subscriber -> mReferenceTribu
                .child(uniqueName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTribu = dataSnapshot.getValue(Tribu.class);
                        subscriber.onNext(mTribu);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }


    //OPEN DIALOG ASKING ABOUT LEAVE TRIBU
    public static void leaveTribu(AppCompatActivity activity) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);
        progress.setMessage("Saindo da mTribu " + mTribu.getProfile().getNameTribu() + "...");

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Deixar Tribu");
        builder.setMessage("Sair da mTribu " + '"' + mTribu.getProfile().getNameTribu() + '"' + "?");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            leaveActualTribu(mTribu, mUser, activity);

        });
        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    //LEAVE ACTUAL TRIBU
    private static void leaveActualTribu(Tribu tribu, User user, AppCompatActivity activity) {

        mReferenceTribusFollowers
                .child(tribu.getProfile().getUniqueName())
                .child(mAuth.getCurrentUser().getUid())
                .removeValue()
                .addOnSuccessListener(aVoid -> {

                    mReferenceFollowers
                            .child(mAuth.getCurrentUser().getUid())
                            .child(tribu.getProfile().getUniqueName())
                            .removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                showProgressDialog(false);
                                removeFollowersInsideTribu(tribu);
                                Toast.makeText(activity.getApplicationContext(), "Você deixou a mTribu " + tribu.getProfile().getNameTribu(), Toast.LENGTH_LONG)
                                        .show();
                                openMainActivity(activity);
                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);
                })
                .addOnFailureListener(Throwable::getLocalizedMessage);
    }


    //REMOVE CURRENT FOLLOWER FROM TRIBUS'S NUMBER OF FOLLOWERS
    private static void removeFollowersInsideTribu(Tribu tribu) {
        mReferenceTribu.child(tribu.getProfile().getUniqueName()).child("profile").child("numFollowers")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        long num = (long) mutableData.getValue();
                        num--;
                        mutableData.setValue(num);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        if (databaseError != null) {
                            databaseError.toException().printStackTrace();
                        }
                    }
                });
    }


    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }

    //OPEN MAIN ACTIVITY AFTER LEAVE TRIBU
    private static void openMainActivity(AppCompatActivity activity) {
        activity.finish();
    }


    //SEND MESSAGE TO FIREBASE
    public static void sendTopicToFirebase(Tribu tribu, ConversationTopic conversationTopic, AppCompatActivity activity) {

        if (conversationTopic != null) {

            progress = new ProgressDialog(activity);
            progress.setCancelable(false);
            progress.setMessage("Criando tópico...");

            showProgressDialog(true);

            Date date = new Date(System.currentTimeMillis());
            conversationTopic.setDate(date);
            String topicKey = mRefTribusTopics.push().getKey();
            conversationTopic.setKey(topicKey);
            conversationTopic.setIdParticipant(mAuth.getCurrentUser().getUid());

            //FirebaseMessaging.getInstance().subscribeToTopic(topicKey);

            //STORE MESSAGE INSIDE TRIBU'S DATABASE
            mRefTribusTopics
                    .child(tribu.getProfile().getUniqueName())
                    .child(topicKey)
                    .setValue(conversationTopic)
                    .addOnSuccessListener(aVoid -> {
                        showProgressDialog(false);
                        Toast.makeText(activity, "Tópico criado!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        showProgressDialog(false);
                        e.getLocalizedMessage();
                    });


        }
    }


    public static FirebaseRecyclerAdapter<ConversationTopic, ConversationTopicVH> getConversationTopics(Tribu tribu,
                                                                                                        ConversationTopicsView view) {

        return mAdapter = new FirebaseRecyclerAdapter<ConversationTopic, ConversationTopicVH>(
                ConversationTopic.class,
                R.layout.row_topic_conversation,
                ConversationTopicVH.class,
                mRefTribusTopics.child(tribu.getProfile().getUniqueName())
        ) {
            @Override
            protected void populateViewHolder(ConversationTopicVH viewHolder, ConversationTopic topic, int position) {

                viewHolder.initViewHolderConversationTopic(tribu, topic, mValueListenerRefUser, mValueListenerRefTribusMessages, view);

                /*mReferenceUser
                        .child(mAuth.getCurrentUser().getUid())
                        .addValueEventListener(mValueListenerRefUser = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshotUser) {

                                mRefTribusMessages
                                        .child(mTribu.getProfile().getUniqueName())
                                        .child(topic.getKey())
                                        .addValueEventListener(mValueListenerRefTribusMessages = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                long numMessages = 0;

                                                if (dataSnapshot.hasChildren()){
                                                     numMessages = dataSnapshot.getChildrenCount();
                                                }

                                                if (topic != null && topic.getIdParticipant() != null) {
                                                    User user = dataSnapshotUser.getValue(User.class);

                                                    viewHolder.setName(user.getUsername(), user.getName());
                                                    viewHolder.setTvTopic(topic.getTopic(), numMessages);
                                                    viewHolder.mIvClock.setVisibility(View.VISIBLE);
                                                    viewHolder.setTvTopicTime(topic.getDate());

                                                }
                                                else {
                                                    viewHolder.setTvTopic(topic.getTopic(), numMessages);
                                                    viewHolder.mIvClock.setVisibility(View.GONE);
                                                }

                                                viewHolder
                                                        .itemView
                                                        .setOnClickListener(v -> {

                                                            FirebaseMessaging.getInstance().subscribeToTopic(topic.getKey());

                                                            //openChatTribuActivity(topic, mTribu, activity, view);

                                                        });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        });*/

            }
        };
    }

    private static void openChatTribuActivity(ConversationTopic topic, Tribu tribu, AppCompatActivity activity, ConversationTopicsView view) {

        Intent intent = new Intent(activity, ChatTribuActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        intent.putExtra(TOPIC_KEY, topic.getKey());

        if(view.mLink != null){
            intent.putExtra(LINK_INTO_MESSAGE, view.mLink);
        }

        activity.startActivity(intent);
    }


    /*public static void createConversationTopic(String mTribusUniqueName, String mTopicKey){

        mRefTribusMessages
                .child(mTribusUniqueName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        String topic = "Conversa sem tópico";

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            MessageUser message = snapshot.getValue(MessageUser.class);

                            if(message.getTopicKey() == null){

                                ConversationTopic conversationTopic = new ConversationTopic();
                                conversationTopic.setTopic(topic);
                                conversationTopic.setIdParticipant(null);
                                conversationTopic.setDate(null);
                                conversationTopic.setKey(mTopicKey);*/

                            /*Map<String, Object> updateMessage = new HashMap<>();
                            updateMessage.put("topic", topic);

                            mRefTribusMessage
                                    .child(mTribusUniqueName)
                                    .child(message.getKey())
                                    .updateChildren(updateMessage)
                                    .addOnFailureListener(Throwable::getLocalizedMessage)
                                    .addOnSuccessListener(aVoid -> {

                                        mRefUsersMessage
                                                .child(mAuth.getCurrentUser().getUid())
                                                .child(mTribusUniqueName)
                                                .child(message.getKey())
                                                .updateChildren(updateMessage)
                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                    });*/

                                /*mRefTribusTopics
                                        .child(mTribusUniqueName)
                                        .child(mTopicKey)
                                        .setValue(conversationTopic)
                                        .addOnFailureListener(Throwable::getLocalizedMessage)
                                        .addOnSuccessListener(aVoid -> {
                                            saveMessageWithTopicIntoFirebase(message, mTribusUniqueName, mTopicKey);
                                        });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }*/

    /*private static void saveMessageWithTopicIntoFirebase(MessageUser message,
                                                         String mTribusUniqueName, String mTopicKey) {
        if(message != null) {
            message.setTopicKey(mTopicKey);

            //STORE MESSAGE INSIDE TRIBU'S DATABASE
            mRefTribusMessages
                    .child(mTribusUniqueName)
                    .child(mTopicKey)
                    .child(message.getKey())
                    .setValue(message)
                    .addOnFailureListener(Throwable::getLocalizedMessage);

            //STORE MESSAGE INSIDE USER'S DATABASE
            mRefUsersMessages
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTribusUniqueName)
                    .child(mTopicKey)
                    .child(message.getKey())
                    .setValue(message)
                    .addOnFailureListener(Throwable::getLocalizedMessage);

        }
    }*/
}