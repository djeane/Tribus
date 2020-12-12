package apptribus.com.tribus.activities.chat_user.mvp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import apptribus.com.tribus.activities.chat_user.adapter.ChatTalkerAdapter;
import apptribus.com.tribus.activities.chat_user.repository.ChatUserAPI;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.send_image_talker.SendImageTalkerActivity;
import apptribus.com.tribus.activities.send_video_talker.SendVideoTalkerActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.USER_ID;

/**
 * Created by User on 6/27/2017.
 */

public class ChatUserModel {

    private final AppCompatActivity activity;
    public static final int REQUEST_RECORD_AUDIO_CHAT_USER = 1;
    private static String[] PERMISSION_RECORD_AUDIO = {Manifest.permission.RECORD_AUDIO};
    //TO REQUEST PERMISSIONS
    public static int GALLERY_REQUEST = 3;
    public static int VIDEO_REQUEST = 4;
    public static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public ChatUserModel(AppCompatActivity activity) {
        this.activity = activity;
    }

    //VERIFY PERMISSION TO ACESS VOICE
    public boolean verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        boolean yes = false;
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSION_RECORD_AUDIO,
                    REQUEST_RECORD_AUDIO_CHAT_USER
            );
            yes = true;
        }
        return yes;
    }


    //GET TALKER
    public Observable<User> getTalker(String userId) {
        return ChatUserAPI.getTalker(userId);
    }


    //GET CURRENT USER
    public Observable<User> getUser() {
        return ChatUserAPI.getUser();
    }

    public void loadData(String mTalkerId, ChatTalkerAdapter mChatTalkerAdapter, List<MessageUser> mMessagesList,
                         RecyclerView mRvChat, int i, int mCurrentPage, LinearLayoutManager mLayoutManager) {
        ChatUserAPI.loadMessages(mTalkerId, mChatTalkerAdapter, mMessagesList, mRvChat, i, mCurrentPage, mLayoutManager);
    }

    public void loadMoreItems(String mTalkerId, ChatTalkerAdapter mChatTalkerAdapter, List<MessageUser> mMessagesList,
                              SwipeRefreshLayout mSwipeLayout, int totalItemsToLoad, int mCurrentPage,
                              LinearLayoutManager mLayoutManager, RecyclerView mRvChat) {
        ChatUserAPI.loadMessagesSwipe(mTalkerId, mChatTalkerAdapter, mMessagesList, mSwipeLayout, totalItemsToLoad,
                mCurrentPage, mLayoutManager, mRvChat);
    }


    //MESSAGES
    //Method to send message to Firebase
    public void sendMessageToFirebase(MessageUser messageUser, String mTalkerId) {
        ChatUserAPI.sendMessageToFirebase(messageUser, mTalkerId);
    }


    //OPEN DETAIL TALKER
    public void openDetailTalker(String mTalkerId) {
        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, mTalkerId);
        intent.putExtra(USER_ID, ChatUserAPI.mCurrentUser.getId());
        activity.startActivity(intent);


    }

    public void backToMainActivity(String mTalkerId){
        ChatUserAPI.backToMainActivity(mTalkerId, activity);
    }


    //VOICE MESSAGE
    //Method that call API to send voice message to Firebase
    public void uploadAudioToFirebase(String fileName, String uniqueID) {
        ChatUserAPI.uploadAudioToFirebase(fileName, uniqueID, activity);
    }

    //ASK FOR PERMISSION IF DON'T GIVE YET
    private static boolean verifyStoragePermissions(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean yes = false;

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
            yes = true;
        }
        return yes;
    }

    public boolean requirePermission() {
        return verifyStoragePermissions(activity);

    }

    //OPEN GALLERY ACTIVITY AND CHOOSE AN IMAGE
    public void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GALLERY_REQUEST);
    }

    //OPEN VIDEO ACTIVITY AND CHOOSE AN IMAGE
    public void getVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, VIDEO_REQUEST);
    }

    //SEND IMAGE TO SEND IMAGE ACTIVITY
    public void openActivitySendImage(Uri uri, int fileSize, String mTalkerId) {

        Intent mediaUpdateImage = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri, activity, SendImageTalkerActivity.class);

        mediaUpdateImage.putExtra("image_size", fileSize);
        mediaUpdateImage.putExtra(CONTACT_ID, mTalkerId);
        mediaUpdateImage.putExtra("cameFrom", "fromShareActivity");
        activity.startActivity(mediaUpdateImage);
        activity.finish();
    }

    public void openActivitySendVideo(Uri uri, int fileSize, String mTalkerId) {

        Intent mediaUpdateVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri, activity, SendVideoTalkerActivity.class);

        mediaUpdateVideo.putExtra("video_size", fileSize);
        mediaUpdateVideo.putExtra(CONTACT_ID, mTalkerId);
        mediaUpdateVideo.putExtra("cameFrom", "fromShareActivity");
        activity.startActivity(mediaUpdateVideo);
        activity.finish();
    }

    public void openBlockUser(User talker) {
            ChatUserAPI.showDialog(activity, talker);

    }

    public void removeListeners(){
        if (ChatUserAPI.mChildListenerRefTalkersMessage != null) {
            ChatUserAPI.mRefTalkersMessage.removeEventListener(ChatUserAPI.mChildListenerRefTalkersMessage);
        }
        if (ChatUserAPI.mChildListenerRefTalkersMessageSwipe != null){
            ChatUserAPI.mRefTalkersMessage.removeEventListener(ChatUserAPI.mChildListenerRefTalkersMessageSwipe);
        }
        if (ChatUserAPI.mValueListenerRefTalker != null){
            ChatUserAPI.mReferenceUser.removeEventListener(ChatUserAPI.mValueListenerRefTalker);
        }
    }

}
