package apptribus.com.tribus.activities.send_image_talker.repository;

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
import apptribus.com.tribus.pojo.Image;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;

/**
 * Created by User on 8/16/2017.
 */

public class SendImageTalkerAPI {


    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private static DatabaseReference mRefUsersTalkMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
    private static StorageReference mStorageImagesTalkers = mStorage.getReference().child(TALKERS_IMAGES_MESSAGES);


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
    public static void uploadImageToFirebase(User userParam, Uri fileName, String description,
                                             AppCompatActivity activity, int imageSize) {

        final String uniqueID = UUID.randomUUID().toString();
        final StorageReference filePath = mStorageImagesTalkers
                .child(userParam.getUsername())
                .child("image")
                .child(uniqueID + ".jpg");
        final String downloadUri = filePath.getPath();
        final String uriDownload = filePath.getStorage().toString();
        addImageToMessage(userParam, null, imageSize, description, activity, fileName, uniqueID);

    }

    //Method to save video path into FirebaseDatabase
    private static void addImageToMessage(User userParam, String downloadUri, long imageSize, String description,
                                          AppCompatActivity activity, Uri fileName, String uniqueID) {

        MessageUser message = new MessageUser(uniqueID + ".jpg", "IMAGE", userParam.getId(), userParam.isAccepted(),
                new Image(description, (int) imageSize, false, false, null));


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
                            )
                            .addOnFailureListener(Throwable::getLocalizedMessage);
                })
                .addOnFailureListener(e -> {
                    e.getLocalizedMessage();
                    Toast.makeText(activity.getApplicationContext(), "Erro ao enviar imagem para Firebase", Toast.LENGTH_LONG).show();
                });
    }

    private static void backToChatUsersActivity(String talkerId, AppCompatActivity activity, Uri fileName, String filePath) {
        Intent intent = new Intent(activity, ChatUserActivity.class);
        intent.setData(fileName);
        intent.putExtra("image_path", filePath);
        intent.putExtra("talker", mTalkerUser.getId());
        activity.startActivity(intent);
        activity.finish();
    }

}
