package apptribus.com.tribus.activities.chat_user.repository;

import android.app.ProgressDialog;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
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

import apptribus.com.tribus.activities.chat_user.adapter.ChatTalkerAdapter;
import apptribus.com.tribus.activities.chat_user.view_holder.UserMessagePrivateViewHolder;
import apptribus.com.tribus.pojo.Audio;
import apptribus.com.tribus.pojo.ChatTalker;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.BLOCKED_TALKERS;
import static apptribus.com.tribus.util.Constantes.CHAT_CURRENT_USER;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.TALKERS_AUDIO_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_IMAGES_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TALKERS_VIDEOS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 6/27/2017.
 */

public class ChatUserAPI {

    //INSTANCES - FIREBASE
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();


    //REFERENCES - FIREBASE
    public static DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    public static DatabaseReference mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
    private static DatabaseReference mRefChatTalker = mDatabase.getReference().child(CHAT_TALKER);
    private static DatabaseReference mRefChatCurrentUser = mDatabase.getReference().child(CHAT_CURRENT_USER);
    private static DatabaseReference mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
    private static DatabaseReference mRefBlockedTalkers = mDatabase.getReference().child(BLOCKED_TALKERS);
    private static StorageReference mStorageVoiceTalkers = mStorage.getReference().child(TALKERS_AUDIO_MESSAGES);
    private static StorageReference mStorageVideoTalkers = mStorage.getReference().child(TALKERS_VIDEOS_MESSAGES);
    private static StorageReference mStorageImagesTalkers = mStorage.getReference().child(TALKERS_IMAGES_MESSAGES);


    //VARIABLES
    private static FirebaseRecyclerAdapter<MessageUser, UserMessagePrivateViewHolder> messagesChat;
    public static Tribu tribu;
    public static User mUserTalker;
    public static User mCurrentUser;
    private static String mTalkerId;
    private static String mUserId;
    private static long mMessageId;
    private static User mUserRight;
    private static User mUserLeft;
    private static File mVideoFolder;
    private static File mAudioFolder;
    private static File mImageFolder;
    private static File videoFromMessage = null;
    private static File audioFromMessage = null;
    private static File imageFromMessage = null;
    private static DatabaseReference messageRef = null;
    private static String uidMessageAnotherUser = null;
    private static String uidSameUser = null;
    private static UserMessagePrivateViewHolder mViewHolder;
    private static Uri uriFromFile = null;


    //SHOW
    private static ProgressDialog progress;

    //LISTENERS
    public static ValueEventListener mValueListenerRefTalker;
    public static ChildEventListener mChildListenerRefTalkersMessage;
    public static ChildEventListener mChildListenerRefTalkersMessageSwipe;


    //GET TALKER
    public static Observable<User> getTalker(String talkerId) {

        mTalkerId = talkerId;
        return Observable
                .create(subscriber -> mReferenceUser
                        .child(talkerId)
                        .orderByKey()
                        .addValueEventListener(mValueListenerRefTalker = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mUserTalker = dataSnapshot.getValue(User.class);
                                subscriber.onNext(mUserTalker);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        }));
    }


    //GET CURRENT USER
    public static Observable<User> getUser() {
        mUserId = mAuth.getCurrentUser().getUid();

        return Observable.create(subscriber ->
                mReferenceUser
                        .child(mAuth.getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                mCurrentUser = dataSnapshot.getValue(User.class);
                                subscriber.onNext(user);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                databaseError.toException().printStackTrace();
                            }
                        }));
    }


    private static void updateNumUnreadMessages(String mTalkerId){
        mRefChatTalker
                .child(mTalkerId)
                .child(mAuth.getCurrentUser().getUid())
                //.child("unreadMessages")
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        ChatTalker chatTalker = (ChatTalker) mutableData.getValue();
                        Date date = new Date(System.currentTimeMillis());

                        if(chatTalker != null){

                            chatTalker.setUnreadMessages(chatTalker.getUnreadMessages() + 1);
                            chatTalker.setDate(date);
                        }
                        else {

                            chatTalker = new ChatTalker();

                            chatTalker.setCurrentUserId(mAuth.getCurrentUser().getUid());
                            chatTalker.setTalkerId(mTalkerId);
                            chatTalker.setDate(date);
                            chatTalker.setTalkerIsOnline(false);
                            chatTalker.setUnreadMessages(1);
                        }

                        //num--;
                        mutableData.setValue(chatTalker);
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

    //SEND MESSAGE TO FIREBASE
    public static void sendMessageToFirebase(MessageUser messageUser, String mTalkerId) {
        Date date = new Date(System.currentTimeMillis());
        String reference = mRefTalkersMessage.push().getKey();
        messageUser.setKey(reference);
        messageUser.setDate(date);

        mRefTalkersMessage
                .child(mUserTalker.getId())
                .child(mCurrentUser.getId())
                .child(reference)
                .setValue(messageUser)
                .addOnSuccessListener(aVoid -> {
                        mRefTalkersMessage
                                .child(mCurrentUser.getId())
                                .child(mUserTalker.getId())
                                .child(reference)
                                .setValue(messageUser)
                                .addOnSuccessListener(aVoid2 -> {

                                    if(!mUserTalker.isOnlineInChat()){
                                        updateNumUnreadMessages(mTalkerId);

                                        mRefChatTalker
                                                .child(mTalkerId)
                                                .child(mAuth.getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        ChatTalker chatTalkerSnapshot = dataSnapshot.getValue(ChatTalker.class);

                                                        Date date = new Date(System.currentTimeMillis());

                                                        Map<String, Object> updateChatTalker = new HashMap<>();
                                                        updateChatTalker.put("currentUserId", mAuth.getCurrentUser().getUid());
                                                        updateChatTalker.put("talkerId", mTalkerId);
                                                        updateChatTalker.put("talkerIsOnline", false);
                                                        updateChatTalker.put("date", date);
                                                        updateChatTalker.put("message", messageUser.getMessage());

                                                        if (!dataSnapshot.hasChildren()){
                                                            updateChatTalker.put("unreadMessages", 1);
                                                        }
                                                        else {
                                                            if (chatTalkerSnapshot.getUnreadMessages() == 0){
                                                                updateChatTalker.put("unreadMessages", 1);
                                                            }
                                                            else {
                                                                updateChatTalker.put("unreadMessages",
                                                                        chatTalkerSnapshot.getUnreadMessages() + 1);
                                                            }
                                                        }

                                                        mRefChatTalker
                                                                .child(mTalkerId)
                                                                .child(mAuth.getCurrentUser().getUid())
                                                                .updateChildren(updateChatTalker)
                                                                .addOnFailureListener(Throwable::getLocalizedMessage);

                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        databaseError.toException().printStackTrace();
                                                    }
                                                });

                                    }

                                })
                                .addOnFailureListener(Throwable::getLocalizedMessage);
                })
                .addOnFailureListener(Throwable::getLocalizedMessage);

    }


    public static void backToMainActivity(String mTalkerId, AppCompatActivity activity){

        if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
            activity.finish();
        }
        else {
            mRefChatTalker
                    .child(mAuth.getCurrentUser().getUid())
                    .child(mTalkerId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChildren()){
                                //ChatTalker chatTalker = dataSnapshot.getValue(ChatTalker.class);
                                //UPDATE ONLINE
                                Map<String, Object> updateChatTalker = new HashMap<>();
                                updateChatTalker.put("unreadMessages", 0);
                                //updateChatTalker.put("talkerIsOnline", mCurrentUser.isOnlineInChat());

                                mRefChatTalker
                                        .child(mAuth.getCurrentUser().getUid())
                                        .child(mTalkerId)
                                        .updateChildren(updateChatTalker)
                                        .addOnSuccessListener(aVoid -> {
                                            activity.finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            e.getLocalizedMessage();
                                            activity.finish();
                                        });
                            }
                            else {
                                activity.finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            databaseError.toException().printStackTrace();
                        }
                    });

        }

    }

    public static void loadMessages(String mTalkerId, ChatTalkerAdapter mChatTalkerAdapter, List<MessageUser> mMessagesList,
                                    RecyclerView mRvChat, int totalItemsToLoad, int mCurrentPage, LinearLayoutManager mLayoutManager) {
        List<String> mKeys = new ArrayList<>();

        mMessagesList.clear();

        mKeys.clear();

        DatabaseReference messageRef = mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId);

        Query messageQuery = messageRef
                .orderByChild("timestamp")
                .limitToLast(mCurrentPage * totalItemsToLoad);

        messageQuery
                .addChildEventListener(mChildListenerRefTalkersMessage = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String keyParam) {

                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        String key = dataSnapshot.getKey();

                        int previousIndex = mKeys.indexOf(keyParam);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mMessagesList.size()) {
                            try {
                                mMessagesList.add(message);
                                mKeys.add(key);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }

                        } else {
                            if (mMessagesList.size() != 0) {

                                try {
                                    mMessagesList.add(nextIndex, message);
                                    mKeys.add(nextIndex, key);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            }
                        }

                        mLayoutManager.scrollToPosition(mChatTalkerAdapter.getItemCount() - 1);

                        //mRvChat.scrollToPosition(mChatTalkerAdapter.getItemCount() - 1);
                        //mLayoutManager.setStackFromEnd(true);
                        mChatTalkerAdapter.notifyItemInserted(nextIndex);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessage);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        String key = dataSnapshot.getKey();
                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        int index = mKeys.indexOf(key);

                        if (mMessagesList.size() != 0) {
                            try {
                                mMessagesList.set(index, message);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }

                        }
                        mChatTalkerAdapter.notifyItemChanged(index);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessage);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        int index = mKeys.indexOf(key);
                        try {
                            mKeys.remove(index);
                        } catch (IndexOutOfBoundsException e) {
                            //Log.e("Error: ", "Valor de error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        if (mMessagesList.size() != 0) {
                            if (index > mMessagesList.size()) {
                                index = mMessagesList.size() + 1;
                                //mMessagesList.remove(index);
                            }

                            if (index == mMessagesList.size()) {
                                try {
                                    index--;
                                    mMessagesList.remove(index);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    mMessagesList.remove(index);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                        mLayoutManager.scrollToPosition(index);

                        mChatTalkerAdapter.notifyItemRemoved(index);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessage);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String keyParam) {

                        String key = dataSnapshot.getKey();
                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        int index = mKeys.indexOf(key);

                        try {
                            mMessagesList.remove(index);
                            mKeys.remove(index);
                        } catch (IndexOutOfBoundsException e) {
                            //Log.e("Error: ", "Valor de error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        int previousIndex = mKeys.indexOf(keyParam);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mMessagesList.size()) {
                            try {
                                mMessagesList.add(message);
                                mKeys.add(key);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            if (mMessagesList.size() != 0) {
                                try {
                                    mMessagesList.add(nextIndex, message);
                                    mKeys.add(nextIndex, key);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            }

                        }
                        mChatTalkerAdapter.notifyItemMoved(index, nextIndex);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessage);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });
    }

    public static void loadMessagesSwipe(String mTalkerId, ChatTalkerAdapter mChatTalkerAdapter, List<MessageUser> mMessagesList,
                                         SwipeRefreshLayout mSwipeLayout, int totalItemsToLoad, int mCurrentPage,
                                         LinearLayoutManager mLayoutManager, RecyclerView mRvChat) {

        List<String> mKeys = new ArrayList<>();
        mMessagesList.clear();
        mKeys.clear();

        int loadItens = mCurrentPage * totalItemsToLoad;

        DatabaseReference messageRef = mRefTalkersMessage
                .child(mAuth.getCurrentUser().getUid())
                .child(mTalkerId);

        Query messageQuery = messageRef
                .orderByChild("timestamp")
                .limitToLast(loadItens);

        messageQuery
                .addChildEventListener(mChildListenerRefTalkersMessageSwipe = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String keyParam) {

                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        String key = dataSnapshot.getKey();

                        int previousIndex = mKeys.indexOf(keyParam);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mMessagesList.size()) {
                            try {
                                mMessagesList.add(message);
                                mKeys.add(key);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }

                        } else {
                            if (mMessagesList.size() != 0) {

                                try {
                                    mMessagesList.add(nextIndex, message);
                                    mKeys.add(nextIndex, key);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            }
                        }
                        mLayoutManager.scrollToPosition(mChatTalkerAdapter.getItemCount() - 1);

                        //mRvChat.scrollToPosition(mChatTalkerAdapter.getItemCount() - 1);
                        //mLayoutManager.setStackFromEnd(true);
                        mSwipeLayout.setRefreshing(false);
                        mChatTalkerAdapter.notifyItemInserted(nextIndex);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessageSwipe);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        String key = dataSnapshot.getKey();
                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        int index = mKeys.indexOf(key);

                        if (mMessagesList.size() != 0) {
                            try {
                                mMessagesList.set(index, message);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }

                        }

                        mSwipeLayout.setRefreshing(false);
                        mChatTalkerAdapter.notifyItemChanged(index);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessageSwipe);

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String key = dataSnapshot.getKey();
                        int index = mKeys.indexOf(key);
                        try {
                            mKeys.remove(index);
                        } catch (IndexOutOfBoundsException e) {
                            //Log.e("Error: ", "Valor de error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        if (mMessagesList.size() != 0) {
                            if (index > mMessagesList.size()) {
                                index = mMessagesList.size() + 1;
                                //mMessagesList.remove(index);
                            }

                            if (index == mMessagesList.size()) {
                                try {
                                    index--;
                                    mMessagesList.remove(index);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    mMessagesList.remove(index);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }

                        mSwipeLayout.setRefreshing(false);
                        mChatTalkerAdapter.notifyItemRemoved(index);
                        mLayoutManager.scrollToPosition(index);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessageSwipe);

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String keyParam) {

                        String key = dataSnapshot.getKey();
                        MessageUser message = dataSnapshot.getValue(MessageUser.class);
                        int index = mKeys.indexOf(key);

                        try {
                            mMessagesList.remove(index);
                            mKeys.remove(index);
                        } catch (IndexOutOfBoundsException e) {
                            //Log.e("Error: ", "Valor de error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        int previousIndex = mKeys.indexOf(keyParam);
                        int nextIndex = previousIndex + 1;
                        if (nextIndex == mMessagesList.size()) {
                            try {
                                mMessagesList.add(message);
                                mKeys.add(key);
                            } catch (IndexOutOfBoundsException e) {
                                //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            if (mMessagesList.size() != 0) {
                                try {
                                    mMessagesList.add(nextIndex, message);
                                    mKeys.add(nextIndex, key);
                                } catch (IndexOutOfBoundsException e) {
                                    //Log.e("Error: ", "Valor de error: " + e.getMessage());
                                    e.printStackTrace();
                                }

                            }

                        }

                        mSwipeLayout.setRefreshing(false);
                        mChatTalkerAdapter.notifyItemMoved(index, nextIndex);
                        mRefTalkersMessage.removeEventListener(mChildListenerRefTalkersMessageSwipe);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException().printStackTrace();
                    }
                });

    }




    //VOICE MESSAGES - user LEFT
    //Method to upload audio
    @SuppressWarnings("VisibleForTests")
    public static void uploadAudioToFirebase(String fileName, String uniqueIDParam, AppCompatActivity activity) {

        File audioFile = new File(fileName);
        Uri uriFromFile = Uri.fromFile(audioFile);

        final String uniqueID = UUID.randomUUID().toString();
        final StorageReference filePath = mStorageVoiceTalkers
                .child(mCurrentUser.getId())
                .child("audio")
                .child(uniqueID + ".3gp");
        final String downloadUri = filePath.getPath();
        int audioDuration = getDuration(audioFile);


        filePath.putFile(uriFromFile)
                .addOnSuccessListener(taskSnapshot -> {
                    StorageReference storageRef = mStorage.getReference().child(downloadUri);
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String duration = stringForTime(audioDuration);
                        addVoiceToMessage(uri, downloadUri, uniqueID, duration);

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


    //Method to save audio path into FirebaseDatabase
    private static void addVoiceToMessage(Uri uri, String downloadUri, String uniqueID, String audioDuration) {

        MessageUser message = new MessageUser(downloadUri, "VOICE", mCurrentUser.getId(),
                mCurrentUser.isAccepted(), new Audio(true, false, String.valueOf(uri), audioDuration));

        String reference = mRefTalkersMessage.push().getKey();
        message.setKey(reference);
        Date date = new Date(System.currentTimeMillis());
        message.setDate(date);

        mRefTalkersMessage
                .child(mCurrentUser.getId())
                .child(mUserTalker.getId())
                .child(reference)
                .setValue(message)
                .addOnSuccessListener(aVoid ->
                        mRefTalkersMessage
                        .child(mUserTalker.getId())
                        .child(mCurrentUser.getId())
                        .child(reference)
                        .setValue(message)
                        .addOnSuccessListener(aVoid1 -> {

                            if(!mUserTalker.isOnlineInChat()){
                                updateNumUnreadMessages(mTalkerId);

                                mRefChatTalker
                                        .child(mTalkerId)
                                        .child(mAuth.getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                ChatTalker chatTalkerSnapshot = dataSnapshot.getValue(ChatTalker.class);

                                                Date date = new Date(System.currentTimeMillis());

                                                Map<String, Object> updateChatTalker = new HashMap<>();
                                                updateChatTalker.put("currentUserId", mAuth.getCurrentUser().getUid());
                                                updateChatTalker.put("talkerId", mTalkerId);
                                                updateChatTalker.put("talkerIsOnline", false);
                                                updateChatTalker.put("date", date);
                                                updateChatTalker.put("message", "Mensagem de Áudio");

                                                if (!dataSnapshot.hasChildren()){
                                                    updateChatTalker.put("unreadMessages", 1);
                                                }
                                                else {
                                                    if (chatTalkerSnapshot.getUnreadMessages() == 0){
                                                        updateChatTalker.put("unreadMessages", 1);
                                                    }
                                                    else {
                                                        updateChatTalker.put("unreadMessages",
                                                                chatTalkerSnapshot.getUnreadMessages() + 1);
                                                    }
                                                }

                                                mRefChatTalker
                                                        .child(mTalkerId)
                                                        .child(mAuth.getCurrentUser().getUid())
                                                        .updateChildren(updateChatTalker)
                                                        .addOnFailureListener(Throwable::getLocalizedMessage);


                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                databaseError.toException().printStackTrace();
                                            }
                                        });

                            }

                        })
                        .addOnFailureListener(Throwable::getLocalizedMessage))
                        .addOnFailureListener(Throwable::getLocalizedMessage);

    }



    public static void showDialog(AppCompatActivity activity, User talker) {
        //INSTANCE OF PROGRESS DIALOG
        progress = new ProgressDialog(activity);
        progress.setCancelable(false);

        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Remover conversa");
        builder.setMessage("Deixar de conversar com " + '"' + talker.getName() + '"' + "?");

        String positiveText = "SIM";
        builder.setPositiveButton(positiveText, (dialog, which) -> {
            dialog.dismiss();
            showProgressDialog(true);
            removeTalker(talker, activity);

        });
        String negativeText = "NÃO";
        builder.setNegativeButton(negativeText, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //SHOW PROGRESS
    private static void showProgressDialog(boolean load) {

        if (load) {
            progress.show();
        } else {
            progress.dismiss();
        }
    }


    private static void removeTalker(User talker, AppCompatActivity activity) {
        //REMOVES TALKER TO CURRENT USER NODE
        mRefUsersTalk
                .child(mAuth.getCurrentUser().getUid())
                .child(talker.getId())
                .removeValue()
                .addOnSuccessListener(aVoid -> {

                    //REMOVES CURRENT USER TO TALKER NODE
                    mRefUsersTalk
                            .child(talker.getId())
                            .child(mAuth.getCurrentUser().getUid())
                            .removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                showProgressDialog(false);
                                Toast.makeText(activity.getApplicationContext(), "Usuário removido com sucesso!", Toast.LENGTH_SHORT)
                                        .show();

                                backToMainActivity(activity);
                            })
                            .addOnFailureListener(Throwable::getLocalizedMessage);
                })
                .addOnFailureListener(Throwable::getLocalizedMessage);

    }

    private static void backToMainActivity(AppCompatActivity activity) {
        activity.finish();

    }


}
