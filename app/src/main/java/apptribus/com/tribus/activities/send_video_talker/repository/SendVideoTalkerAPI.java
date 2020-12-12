package apptribus.com.tribus.activities.send_video_talker.repository;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.UUID;

import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.activities.chat_user.ChatUserActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.pojo.Video;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_VIDEOS_MESSAGES;

/**
 * Created by User on 8/16/2017.
 */

public class SendVideoTalkerAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static StorageReference mStorageVideosTalkers = mStorage.getReference().child(TALKERS_VIDEOS_MESSAGES);
    private static DatabaseReference mRefUsersTalkMessage = mDatabase.getReference().child(TALKERS_MESSAGES);

    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu tribu;
    private static User mUser;
    private static long mTribusKey;
    private static ProgressDialog progress;
    private static User mTalkerUser;


    //GET TALKER
    public static Observable<User> getTalkerUser(String talkerId) {
        return Observable.create(subscriber ->
                mReferenceUser
                        .child(talkerId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTalkerUser = dataSnapshot.getValue(User.class);
                                subscriber.onNext(mTalkerUser);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        }));
    }

    //GET CURRENT USER
    public static Observable<User> getCurrentUser() {
        String uid = mAuth.getCurrentUser().getUid();
        return Observable.create(subscriber ->
                mReferenceUser
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
                                             AppCompatActivity activity, int videoSize) {

        final String uniqueID = UUID.randomUUID().toString();
        final StorageReference filePath = mStorageVideosTalkers
                .child(userParam.getUsername())
                .child("video")
                .child(uniqueID + ".mp4");
        final String downloadUri = filePath.getPath();
        final String uriDownload = filePath.getStorage().toString();
        addVideoToMessage(userParam, downloadUri, videoSize, description, duration, activity, fileName, uniqueID);

    }

    //Method to save video path into FirebaseDatabase
    private static void addVideoToMessage(User userParam, String downloadUri, long videoSize, String description,
                                          String duration, AppCompatActivity activity, Uri fileName, String uniqueID) {

        MessageUser message = new MessageUser(uniqueID + ".mp4", "VIDEO", userParam.getId(), userParam.isAccepted(),
                new Video(description, (int)videoSize, duration, false, false, null));

        String reference = mRefUsersTalkMessage.push().getKey();

        message.setKey(reference);
        Date date = new Date(System.currentTimeMillis());
        message.setDate(date);


        mRefUsersTalkMessage
                .child(mUser.getId())
                .child(mTalkerUser.getId())
                .child(reference)
                .setValue(message)
                .addOnSuccessListener(aVoid -> {
                    mRefUsersTalkMessage
                            .child(mTalkerUser.getId())
                            .child(mUser.getId())
                            .child(reference)
                            .setValue(message)
                            .addOnSuccessListener(aVoid1 ->
                                    backToChatUsersActivity(userParam.getId(), activity, fileName, uniqueID)
                            ).addOnFailureListener(Throwable::getLocalizedMessage);
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(activity.getApplicationContext(), "Erro ao enviar vídeo.", Toast.LENGTH_LONG).show();

                });
    }

    private static void backToChatUsersActivity(String talkerId, AppCompatActivity activity, Uri fileName, String filePath) {
        Intent intent = new Intent(activity, ChatUserActivity.class);
        intent.setData(fileName);
        intent.putExtra("video_path", filePath);
        intent.putExtra("talker", mTalkerUser.getId());
        activity.startActivity(intent);
        activity.finish();
    }

}
