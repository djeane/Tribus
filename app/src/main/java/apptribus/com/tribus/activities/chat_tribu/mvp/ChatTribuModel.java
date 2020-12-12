package apptribus.com.tribus.activities.chat_tribu.mvp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import apptribus.com.tribus.activities.change_admin.ChangeAdminActivity;
import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.chat_tribu.repository.ChatTribuAPI;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.detail_tribu_add_followers.DetailTribuAddFollowersActivity;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.activities.profile_tribu_admin.ProfileTribuAdminActivity;
import apptribus.com.tribus.activities.profile_tribu_follower.ProfileTribuFollowerActivity;
import apptribus.com.tribus.activities.send_image.SendImageActivity;
import apptribus.com.tribus.activities.send_video.SendVideoActivity;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.firestore.FirestoreService;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.FROM_CHAT_TRIBUS;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.USER_ID;

public class ChatTribuModel {

    private final AppCompatActivity activity;
    public static final int REQUEST_RECORD_AUDIO = 1;
    private static String[] PERMISSION_RECORD_AUDIO = {Manifest.permission.RECORD_AUDIO};
    private FirebaseAuth mAuth;



    //TO REQUEST PERMISSIONS
    public static int GALLERY_REQUEST = 3;
    public static int VIDEO_REQUEST = 4;
    public static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FirestoreService mFirestoreService;

    public ChatTribuModel(AppCompatActivity activity) {
        this.activity = activity;
        mFirestoreService = new FirestoreService(activity);
        mAuth = FirebaseAuth.getInstance();
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
                    REQUEST_RECORD_AUDIO
            );
            yes = true;
        }
        return yes;
    }


    //OBSERVABLES
    //Get the current mTribu
    public Observable<Tribu> getTribu(String tribuKey) {
        return mFirestoreService.getTribu(tribuKey);
    }


    public Observable<User> getReplyMessageUser(MessageUser messageUser){
        return ChatTribuAPI.getReplyMessageUser(messageUser, activity);
    }

    public Observable<List<MessageUser>> loadMessages(List<MessageUser> mMessagesList, ChatTribuView view,
                                                      ChatTribuAdapter mChatTribuAdapter){

        return ChatTribuAPI.loadMessages(mMessagesList, activity, view, mChatTribuAdapter);
    }

    public void loadMessages(List<MessageUser> mMessagesList, ChatTribuView view, ChatTribuPresenter presenter){

        //ChatTribuAPI.loadMessages(mMessagesList, activity, view, presenter);
    }


    public void loadMoreMessages(List<MessageUser> mMessagesList, ChatTribuView view, ChatTribuAdapter mChatTribuAdapter){

        ChatTribuAPI.loadMoreMessages(mMessagesList, activity, view, mChatTribuAdapter);
    }


    //Get the current user
    public Observable<User> getUser() {
        return mFirestoreService.getCurrentUser(mAuth.getCurrentUser().getUid());
    }


    //MESSAGES
    //Method to send message to Firebase
    public void sendMessageToFirestore(MessageUser messageUser, String mTopicKey, String mTribuKey) {
        ChatTribuAPI.sendMessageToFirestore(messageUser, mTopicKey, mFirestoreService, mTribuKey);
    }


    public void openDetailTribu(Tribu tribu) {
        Intent intent = new Intent(activity, ProfileTribuFollowerActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void openDetailTribuAddFollowers(Tribu tribu) {
        Intent intent = new Intent(activity, DetailTribuAddFollowersActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void backToMainActivity(String fromNotification) {
        if (fromNotification != null){
            Intent intent = new Intent(activity, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();

        }
        else {
            activity.finish();
        }

    }

    public void openChangeAdminActivity(Tribu tribu) {
        Intent intent = new Intent(activity, ChangeAdminActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void openProfileTribuAdminActivity(Tribu tribu) {
        Intent intent = new Intent(activity, ProfileTribuAdminActivity.class);
        intent.putExtra(TRIBU_UNIQUE_NAME, tribu.getProfile().getUniqueName());
        activity.startActivity(intent);
    }

    public void backToMainActivity(){
        activity.finish();
    }


    //VOICE MESSAGE
    //Method that call API to send voice message to Firebase
    public void uploadAudioToFirebase(String fileName, String uniqueID, String mTopicKey) {
        ChatTribuAPI.uploadAudioToFirebase(fileName, uniqueID, mTopicKey, mFirestoreService);
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
    public void openActivitySendImage(Uri uri, int fileSize, String mTribusKey, String mTopicKey) {

        Intent mediaUpdateVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri, activity, SendImageActivity.class);

        mediaUpdateVideo.putExtra("image_size", fileSize);
        mediaUpdateVideo.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mediaUpdateVideo.putExtra(TOPIC_KEY, mTopicKey);
        mediaUpdateVideo.putExtra("cameFrom", "fromShareActivity");
        //mediaUpdateVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(mediaUpdateVideo);
        activity.finish();
    }

    public void openActivitySendVideo(Uri uri, int fileSize, String mTribusKey, String mTopicKey) {

        Intent mediaUpdateVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                uri, activity, SendVideoActivity.class);

        mediaUpdateVideo.putExtra("video_size", fileSize);
        mediaUpdateVideo.putExtra(TRIBU_UNIQUE_NAME, mTribusKey);
        mediaUpdateVideo.putExtra(TOPIC_KEY, mTopicKey);
        mediaUpdateVideo.putExtra("cameFrom", "fromShareActivity");
        //mediaUpdateVideo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(mediaUpdateVideo);
        activity.finish();
    }

    public void openDetailUserActivity(String contactId, String userId, String tribuKey){

        Intent intent = new Intent(activity, DetailTalkerActivity.class);
        intent.putExtra(CONTACT_ID, contactId);
        intent.putExtra(FROM_CHAT_TRIBUS, FROM_CHAT_TRIBUS);
        intent.putExtra(TRIBU_KEY, tribuKey);
        intent.putExtra(USER_ID, userId);
        activity.startActivity(intent);
    }


}
