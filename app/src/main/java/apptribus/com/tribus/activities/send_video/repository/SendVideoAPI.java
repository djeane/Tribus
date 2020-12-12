package apptribus.com.tribus.activities.send_video.repository;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;

import apptribus.com.tribus.activities.chat_tribu.ChatTribuActivity;
import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.Video;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_VIDEOS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.VIDEOS_USERS;

/**
 * Created by User on 7/12/2017.
 */

public class SendVideoAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE COLLECTIONS REFERENCES
    private static CollectionReference mTribusCollections = mFirestore.collection(GENERAL_TRIBUS);


    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
    private static StorageReference mStorageVideosUsers = mStorage.getReference().child(VIDEOS_USERS);
    private static StorageReference mStorageVideosTribus = mStorage.getReference().child(TRIBUS_VIDEOS_MESSAGES);


    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu mTribu;
    private static User mUser;
    private static long mTribusKey;
    private static ProgressDialog progress;

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

    //GET USER(FOLLOWER)
    public static Observable<User> getUser() {
        String uid = mAuth.getCurrentUser().getUid();
        return Observable.create(subscriber -> mReferenceUser
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        subscriber.onNext(mUser);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                }));
    }

    //VIDEO MESSAGES
    //Method to upload video
    @SuppressWarnings("VisibleForTests")
    public static void uploadVideoToFirebase(User userParam, Uri fileName, String description, String duration,
                                             AppCompatActivity activity, int videoSize, String mTopicKey, String mTribuKey) {

        final String uniqueID = UUID.randomUUID().toString();
        final StorageReference filePath = mStorageVideosTribus
                .child(mTribuKey)
                .child(mTopicKey)
                .child(userParam.getUsername())
                .child("video")
                .child(uniqueID + ".mp4");
        final String downloadUri = filePath.getPath();
        final String uriDownload = filePath.getStorage().toString();
        addVideoToMessage(userParam, downloadUri, videoSize, description, duration, activity, fileName, uniqueID, mTopicKey);

    }




    //SEND VIDEO MESSAGE TO FIRESTORE
    private static void addVideoToMessage(User userParam, String downloadUri, long videoSize, String description,
                                          String duration, AppCompatActivity activity, Uri fileName, String uniqueID, String mTopicKey) {

        MessageUser message = new MessageUser(uniqueID + ".mp4", "VIDEO", userParam.getId(), userParam.isAccepted(),
                new Video(description, (int)videoSize, duration, false, false, null));

        String pushVideoMessage = mRefTribusMessage.push().getKey();

        message.setKey(pushVideoMessage);
        Date date = new Date(System.currentTimeMillis());
        message.setDate(date);
        message.setTopicKey(mTopicKey);


        mTribusCollections
                .document(mTribu.getKey())
                .collection(TOPICS)
                .document(mTopicKey)
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .set(message)
                .addOnSuccessListener(aVoid -> {
                    backToChatTribusActivity(mTribu.getProfile().getUniqueName(), activity, fileName, uniqueID, mTopicKey);
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(activity.getApplicationContext(), "Erro ao enviar vídeo.", Toast.LENGTH_LONG).show();

                });

    }

    //Method to save video path into FirebaseDatabase
    /*private static void addVideoToMessage(User userParam, String downloadUri, long videoSize, String description,
                                          String duration, AppCompatActivity activity, Uri fileName, String uniqueID, String mTopicKey) {

        MessageUser message = new MessageUser(uniqueID + ".mp4", "VIDEO", userParam.getId(), userParam.isAccepted(),
                new Video(description, (int)videoSize, duration, false, false, null));

        String pushVideoMessage = mRefTribusMessage.push().getKey();

        message.setKey(pushVideoMessage);
        Date date = new Date(System.currentTimeMillis());
        message.setDate(date);
        message.setTopicKey(mTopicKey);


        mRefTribusMessage
                .child(mTribu.getProfile().getUniqueName())
                .child(mTopicKey)
                .child(pushVideoMessage)
                .setValue(message)
                .addOnSuccessListener(aVoid -> {
                    backToChatTribusActivity(mTribu.getProfile().getUniqueName(), activity, fileName, uniqueID, mTopicKey);
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(activity.getApplicationContext(), "Erro ao enviar video para Firebase", Toast.LENGTH_LONG).show();

                });
    }*/



    private static void backToChatTribusActivity(String uniqueName, AppCompatActivity activity, Uri fileName, String filePath, String mTopicKey) {
        Intent intent = new Intent(activity, ChatTribuActivity.class);
        intent.setData(fileName);
        intent.putExtra("video_path", filePath);
        intent.putExtra(TRIBU_UNIQUE_NAME, uniqueName);
        intent.putExtra(TOPIC_KEY, mTopicKey);
        activity.startActivity(intent);
        activity.finish();
    }

}
