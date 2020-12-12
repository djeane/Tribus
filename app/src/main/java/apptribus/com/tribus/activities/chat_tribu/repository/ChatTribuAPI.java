package apptribus.com.tribus.activities.chat_tribu.repository;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuPresenter;
import apptribus.com.tribus.activities.chat_tribu.mvp.ChatTribuView;
import apptribus.com.tribus.activities.chat_tribu.view_holder.UserMessageViewHolder;
import apptribus.com.tribus.pojo.Audio;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static android.view.View.GONE;
import static apptribus.com.tribus.util.Constantes.DATE;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REPLY;
import static apptribus.com.tribus.util.Constantes.REPLY_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TOPICS;
import static apptribus.com.tribus.util.Constantes.TOPIC_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_AUDIO_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;

public class ChatTribuAPI {

    //INSTANCES - FIREBASE
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();

    //FIRESTORE INSTANCE
    private static FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

    //FIRESTORE REFERENCES
    private static CollectionReference mTribusCollection = mFirestore.collection(GENERAL_TRIBUS);
    private static CollectionReference mUserCollection = mFirestore.collection(GENERAL_USERS);


    //REFERENCES - FIREBASE
    private static DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    public static DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
    private static DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
    private static StorageReference mStorageVoiceTribus = mStorage.getReference().child(TRIBUS_AUDIO_MESSAGES);

    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessageViewHolder> messagesChat;
    public static Tribu mTribu;
    private static User mUser;

    //SHOW
    private static ProgressDialog progress;


    private static DocumentSnapshot mLastMessageVisible;

    private static ChatTribuAdapter mChatTribuAdapter;
    private static Integer mCurrentIndex = 0;


    public static Observable<User> getReplyMessageUser(MessageUser messageUser, Context context) {
        return Observable.create(subscriber ->

                mUserCollection
                        .document(messageUser.getUidUser())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                subscriber.onNext(documentSnapshot.toObject(User.class));
                            } else {
                                subscriber.onNext(null);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context.getApplicationContext(), "Houve um erro ao citar essa mensagem.",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        })

        );

    }


    public static Observable<List<MessageUser>> loadMessages(List<MessageUser> mMessagesList, AppCompatActivity activity,
                                                             ChatTribuView view, ChatTribuAdapter mChatTribuAdapter) {

        return Observable.create(subscriber -> {

            mTribusCollection
                    .document(view.mTribuKey)
                    .collection(TOPICS)
                    .document(view.mTopicKey)
                    .collection(TOPIC_MESSAGES)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .limit(5)
                    .addSnapshotListener(activity, (queryDocumentSnapshots, e1) -> {

                        if (e1 != null) {
                            subscriber.onNext(null);
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                                MessageUser messageUser = dc.getDocument().toObject(MessageUser.class);
                                int oldIndex = dc.getOldIndex();
                                int newIndex = dc.getNewIndex();

                                switch (dc.getType()) {
                                    case ADDED:
                                        if (newIndex == mMessagesList.size()) {
                                            try {
                                                mMessagesList.add(newIndex, messageUser);
                                            } catch (IndexOutOfBoundsException ex) {
                                                ex.printStackTrace();
                                                Toast.makeText(activity.getApplicationContext(), "Houve um erro.",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            Log.e("tribus: ", "newIndex do tamanho da lista.");
                                            Log.e("tribus: ", "newIndex: " + newIndex);
                                            Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                            Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);

                                        } else {
                                            try {
                                                mMessagesList.add(0, messageUser);
                                                        /*if (mChatTribuAdapter != null) {
                                                            mChatTribuAdapter.notifyItemInserted(0);
                                                        }*/

                                                view.mLayoutManager.scrollToPosition(0);
                                            } catch (IndexOutOfBoundsException ex) {
                                                ex.printStackTrace();
                                                Toast.makeText(activity.getApplicationContext(), "Houve um erro." +
                                                                " new Index diferente do tamanho da lista",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            Log.e("tribus: ", "newIndex maior que o tamanho da lista.");
                                            Log.e("tribus: ", "newIndex: " + newIndex);
                                            Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                            Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);


                                        }
                                        break;

                                case MODIFIED:
                                    if (mChatTribuAdapter != null) {
                                        mChatTribuAdapter.notifyDataSetChanged();
                                    }
                                    break;

                                }

                                mCurrentIndex++;

                            }

                            mLastMessageVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                            subscriber.onNext(mMessagesList);
                        /*mChatTribuAdapter = new ChatTribuAdapter(activity.getApplicationContext(),
                                mMessagesList, view.mTribuKey, view.mVideoUri, view.mVideoPath,
                                view.mImageUri, view.mImagePath, view, view.mTopicKey, presenter,
                                tribu.getProfile().isPublic());

                        view.mRvChat.setAdapter(mChatTribuAdapter);*/

                        } else {
                            subscriber.onNext(null);
                        }

                    });


        });
    }


    public static void loadMessages(List<MessageUser> mMessagesList, AppCompatActivity activity, ChatTribuView view,
                                    ChatTribuPresenter presenter) {

        mTribusCollection
                .document(view.mTribuKey)
                .addSnapshotListener(activity, (documentSnapshot, e) -> {

                    if (e != null) {
                        return;
                    }

                    Tribu tribu = documentSnapshot.toObject(Tribu.class);

                    mTribusCollection
                            .document(view.mTribuKey)
                            .collection(TOPICS)
                            .document(view.mTopicKey)
                            .collection(TOPIC_MESSAGES)
                            .orderBy(DATE, Query.Direction.DESCENDING)
                            .limit(5)
                            .addSnapshotListener(activity, (queryDocumentSnapshots, e1) -> {

                                if (e1 != null) {
                                    return;
                                }

                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                                        MessageUser messageUser = dc.getDocument().toObject(MessageUser.class);
                                        int oldIndex = dc.getOldIndex();
                                        int newIndex = dc.getNewIndex();

                                        switch (dc.getType()) {
                                            case ADDED:
                                                if (newIndex == mMessagesList.size()) {
                                                    try {
                                                        mMessagesList.add(newIndex, messageUser);
                                                    } catch (IndexOutOfBoundsException ex) {
                                                        ex.printStackTrace();
                                                        Toast.makeText(activity.getApplicationContext(), "Houve um erro.",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                    Log.e("tribus: ", "newIndex do tamanho da lista.");
                                                    Log.e("tribus: ", "newIndex: " + newIndex);
                                                    Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                                    Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);

                                                } else {
                                                    try {
                                                        mMessagesList.add(0, messageUser);
                                                        /*if (mChatTribuAdapter != null) {
                                                            mChatTribuAdapter.notifyItemInserted(0);
                                                        }*/

                                                        view.mLayoutManager.scrollToPosition(0);
                                                    } catch (IndexOutOfBoundsException ex) {
                                                        ex.printStackTrace();
                                                        Toast.makeText(activity.getApplicationContext(), "Houve um erro." +
                                                                        " new Index diferente do tamanho da lista",
                                                                Toast.LENGTH_SHORT).show();
                                                    }

                                                    Log.e("tribus: ", "newIndex maior que o tamanho da lista.");
                                                    Log.e("tribus: ", "newIndex: " + newIndex);
                                                    Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                                    Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);


                                                }
                                                break;

                                            case MODIFIED:
                                                if (mChatTribuAdapter != null) {
                                                    mChatTribuAdapter.notifyDataSetChanged();
                                                }
                                                break;

                                        }

                                        mCurrentIndex++;

                                    }

                                    mLastMessageVisible = queryDocumentSnapshots.getDocuments()
                                            .get(queryDocumentSnapshots.size() - 1);

                                    mChatTribuAdapter = new ChatTribuAdapter(activity.getApplicationContext(),
                                            mMessagesList, view.mTribuKey, view.mVideoUri, view.mVideoPath,
                                            view.mImageUri, view.mImagePath, view, view.mTopicKey, presenter,
                                            tribu.getProfile().isPublic());

                                    view.mRvChat.setAdapter(mChatTribuAdapter);

                                }

                            });


                });


    }


    public static void loadMoreMessages(List<MessageUser> mMessagesList, AppCompatActivity activity, ChatTribuView view,
                                        ChatTribuAdapter mChatTribuAdapter) {

        List<MessageUser> listMessages = new ArrayList<>();

        if (mLastMessageVisible != null) {

            mTribusCollection
                    .document(view.mTribuKey)
                    .collection(TOPICS)
                    .document(view.mTopicKey)
                    .collection(TOPIC_MESSAGES)
                    .orderBy(DATE, Query.Direction.DESCENDING)
                    .startAfter(mLastMessageVisible)
                    .limit(5)
                    .addSnapshotListener(activity, (queryDocumentSnapshots, e) -> {

                        if (e != null) {
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {

                            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                                MessageUser messageUser = dc.getDocument().toObject(MessageUser.class);
                                int oldIndex = dc.getOldIndex();
                                int newIndex = dc.getNewIndex();

                                switch (dc.getType()) {
                                    case ADDED:
                                        if (newIndex == listMessages.size()) {

                                            try {
                                                listMessages.add(newIndex, messageUser);
                                                mMessagesList.add(mCurrentIndex, messageUser);
                                                //mChatTribuAdapter.notifyItemInserted(mCurrentIndex);

                                            } catch (IndexOutOfBoundsException ex) {
                                                ex.printStackTrace();
                                            }


                                            Log.e("tribus: ", "newIndex do tamanho da lista.");
                                            Log.e("tribus: ", "newIndex: " + newIndex);
                                            Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                            Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);

                                        } else {
                                            try {
                                                mMessagesList.add(0, messageUser);
                                                //mChatTribuAdapter.notifyItemInserted(0);
                                                view.mLayoutManager.scrollToPosition(0);

                                            } catch (IndexOutOfBoundsException ex) {
                                                ex.printStackTrace();
                                            }

                                            Log.e("tribus: ", "newIndex maior que o tamanho da lista.");
                                            Log.e("tribus: ", "newIndex: " + newIndex);
                                            Log.e("tribus: ", "mMessagesList.size(): " + mMessagesList.size());
                                            Log.e("tribus: ", "mCurrentIndex: " + mCurrentIndex);

                                        }

                                        break;

                                    /*case MODIFIED:
                                        mChatTribuAdapter.notifyDataSetChanged();
                                        view.mProgressBarTop.setVisibility(GONE);
                                        break;*/

                                }

                                mCurrentIndex++;
                            }
                            mLastMessageVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);

                        }
                        view.mProgressBarTop.setVisibility(GONE);
                    });

            mLastMessageVisible = null;

        }
        else {
            view.mProgressBarTop.setVisibility(GONE);
        }
    }


    //SEND MESSAGE TO FIRESTORE
    public static void sendMessageToFirestore(MessageUser messageUser, String mTopicKey, FirestoreService mFirestoreService,
                                              String mTribuKey) {


        if (messageUser != null) {
            if (messageUser.getContentType().equals(TEXT) || (messageUser.getContentType().equals(TEXT))) {
                Date date = new Date(System.currentTimeMillis());
                messageUser.setDate(date);

                String messageKey = mReferenceTribu.push().getKey();
                messageUser.setKey(messageKey);

                mFirestoreService.addMessagesIntoTopics(mTribuKey, mTopicKey, messageUser);
            }
            if (messageUser.getContentType().equals(MESSAGE_REPLY)) {

                Date date = new Date(System.currentTimeMillis());
                messageUser.setDate(date);

                String messageKey = mReferenceTribu.push().getKey();
                messageUser.setKey(messageKey);

                addMessageReplyIntoTopics(mTribuKey, mTopicKey, messageUser);

            }

        }
    }

    private static void addMessageReplyIntoTopics(String tribuKey, String topicKey, MessageUser message) {

        Map<String, Object> newReply = new HashMap<>();
        newReply.put("messageReplied", message.getKey());


        mTribusCollection
                .document(tribuKey)
                .collection(TOPICS)
                .document(topicKey)
                .collection(TOPIC_MESSAGES)
                .document(message.getKey())
                .set(message)
                .addOnSuccessListener(aVoid -> {

                    mTribusCollection
                            .document(tribuKey)
                            .collection(TOPICS)
                            .document(topicKey)
                            .collection(TOPIC_MESSAGES)
                            .document(message.getReplyMessageKey())
                            .collection(REPLY_MESSAGES)
                            .document(message.getReplyUserId())
                            .set(newReply)
                            .addOnFailureListener(e -> {

                                mTribusCollection
                                        .document(tribuKey)
                                        .collection(TOPICS)
                                        .document(topicKey)
                                        .collection(TOPIC_MESSAGES)
                                        .document(message.getKey())
                                        .delete();

                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("tribu: ", "Erro ao enviar menssagem: " + e.getMessage());
                });
    }


    //SHOW PROGRESS
    /*private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }*/


    //OPEN DIALOG ASKING ABOUT LEAVE TRIBU
    /*public static void leaveTribu(AppCompatActivity activity) {
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
    }*/


    //LEAVE ACTUAL TRIBU
    /*private static void leaveActualTribu(Tribu tribu, User user, AppCompatActivity activity) {

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
    }*/


    //REMOVE CURRENT FOLLOWER FROM TRIBUS'S NUMBER OF FOLLOWERS
    /*private static void removeFollowersInsideTribu(Tribu tribu) {
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
    }*/


    //OPEN MAIN ACTIVITY AFTER LEAVE TRIBU
    /*private static void openMainActivity(AppCompatActivity activity) {
        activity.finish();
    }*/

    //VOICE MESSAGES - user LEFT
    //Method to upload audio
    @SuppressWarnings("VisibleForTests")
    public static void uploadAudioToFirebase(String name, String fileName, String mTopicKey,
                                             FirestoreService mFirestoreService) {

        File audioFile = new File(fileName);
        Uri uriFromFile = Uri.fromFile(audioFile);
        final String uniqueID = UUID.randomUUID().toString();
        final StorageReference filePath = mStorageVoiceTribus
                .child(mTopicKey)
                .child(mUser.getUsername())
                .child("audio")
                .child(uniqueID + ".3gp");
        final String downloadUri = filePath.getPath();
        int audioDuration = getDuration(audioFile);


        filePath.putFile(uriFromFile)
                .addOnSuccessListener(taskSnapshot -> {
                    StorageReference storageRef = mStorage.getReference().child(downloadUri);
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String duration = stringForTime(audioDuration);
                                addVoiceToMessageIntoFirestore(uri, downloadUri, uniqueID, duration, mTopicKey, mFirestoreService);

                            }).addOnFailureListener(Throwable::getLocalizedMessage);


                })
                .addOnFailureListener(Throwable::getLocalizedMessage);

    }

    private static int getDuration(File file) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return (int) Long.parseLong(durationStr);
    }

    private static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    //SEND VOICE MESSAGE TO FIRESTORE
    private static void addVoiceToMessageIntoFirestore(Uri uri, String downloadUri, String uniqueID,
                                                       String duration, String mTopicKey,
                                                       FirestoreService mFirestoreService) {

        MessageUser message = new MessageUser(downloadUri, "VOICE", mUser.getId(),
                mUser.isAccepted(), new Audio(true, false, String.valueOf(uri), duration));

        String messageKey = mRefTribusMessage.child(mTribu.getProfile().getUniqueName()).push().getKey();
        message.setKey(messageKey);
        Date date = new Date(System.currentTimeMillis());
        message.setDate(date);
        message.setTopicKey(mTopicKey);

        mFirestoreService.addMessagesIntoTopics(mTribu.getKey(), mTopicKey, message);
    }
}
