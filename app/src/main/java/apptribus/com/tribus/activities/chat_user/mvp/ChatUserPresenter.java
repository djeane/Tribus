package apptribus.com.tribus.activities.chat_user.mvp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_user.adapter.ChatTalkerAdapter;
import apptribus.com.tribus.activities.chat_user.camera.CamTalkerActivity;
import apptribus.com.tribus.activities.chat_user.view_holder.UserMessagePrivateViewHolder;
import apptribus.com.tribus.activities.detail_talker.DetailTalkerActivity;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.FindUrl;
import apptribus.com.tribus.util.GetTimeAgo;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.PresenceSytemAndLastSeenChatTalker;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.CHAT_TALKER;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LINK;
import static apptribus.com.tribus.util.Constantes.TALKERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.UPDATE_LAST_MESSAGE;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

/**
 * Created by User on 6/27/2017.
 */

public class ChatUserPresenter implements Toolbar.OnMenuItemClickListener {

    private final ChatUserView view;
    private final ChatUserModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseRecyclerAdapter<MessageUser, UserMessagePrivateViewHolder> mAdapter;
    private boolean response;
    private String message;
    private String mFileName = null;
    private MediaRecorder mRecorder;
    public boolean isPaused;
    private String uniqueID;
    public static boolean isOpen;
    private User mUserTalker;
    public User mUser;
    private String mLinkPresenter;
    private String mLinkPresenterNoChanges;
    private boolean comingFromOnRestart = false;
    private MenuItem mMenuItemRemoveTalker;
    private MenuItem mMenuItemProfileTalker;


    //ADAPTER
    private List<MessageUser> mMessagesList = new ArrayList<>();
    private List<MessageUser> mMessagesSwipeList;
    private ChatTalkerAdapter mChatTalkerAdapter;
    private ChatTalkerAdapter mChatTalkerSwipeAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;



    //REFERENCES - FIREBASE (KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    public DatabaseReference mReferenceChatTalker = mDatabase.getReference().child(CHAT_TALKER);
    private DatabaseReference mRefTalkersMessage = mDatabase.getReference().child(TALKERS_MESSAGES);
    private DatabaseReference mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);


    public ChatUserPresenter(ChatUserView view, ChatUserModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        if (!comingFromOnRestart) {

            //KEEP SYNCED
            mReferenceUser.keepSynced(true);
            mRefTalkersMessage.keepSynced(true);
            mRefUsersTalk.keepSynced(true);

            PresenceSystemAndLastSeen.presenceSystem();
            PresenceSytemAndLastSeenChatTalker.presenceSystem();

            subscription.add(observeTalker());
            subscription.add(observeBtnSend());
            subscription.add(observeClickBtnVoiceRecord());
            //subscription.add(observeETChat());
            //subscription.add(observeIbCamera());
            //subscription.add(observeToolbarClicks());
            subscription.add(observeIbShare());
            subscription.add(observeBtnArrowBack());
            subscription.add(observeUserImage());
            setAdapter();

            isOpen = true;


            NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancel(2);
            notificationManager.cancel(5);

        }

        comingFromOnRestart = false;
    }

    public void onResume(){

        PresenceSystemAndLastSeen.presenceSystem();
        PresenceSytemAndLastSeenChatTalker.presenceSystem();

        subscription.add(populateRecyclerView());

        view.mSwipeLayout.setOnRefreshListener(this::setAdapterSwipe);

        if(view.mLink != null && !view.mLink.equals("")) {
            setLink();
        }
    }

    public void onPause(){
        PresenceSystemAndLastSeen.lastSeen();
        PresenceSytemAndLastSeenChatTalker.lastSeen();
        mMessagesSwipeList = null;

    }

    public void onStop() {
        PresenceSytemAndLastSeenChatTalker.lastSeen();

        model.removeListeners();

        isOpen = false;

    }

    public void onRestart(){

        comingFromOnRestart = true;

        //KEEP SYNCED
        mReferenceUser.keepSynced(true);
        mRefTalkersMessage.keepSynced(true);
        mRefUsersTalk.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();
        PresenceSytemAndLastSeenChatTalker.presenceSystem();

        subscription.add(observeTalker());
        subscription.add(observeBtnSend());
        subscription.add(observeClickBtnVoiceRecord());
        //subscription.add(observeETChat());
        //subscription.add(observeIbCamera());
        //subscription.add(observeToolbarClicks());
        subscription.add(observeIbShare());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeIvContacts());
        subscription.add(observeIvDocs());
        subscription.add(observeIvLocation());
        subscription.add(observeUserImage());
        setAdapter();

        isOpen = true;


        NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


        if(mMessagesSwipeList == null && mChatTalkerSwipeAdapter != null) {
            Toast.makeText(view.mContext,
                    "Se o conteúdo não carregar, atualize a tela arrastando-a de cima para baixo.",
                    Toast.LENGTH_LONG)
                    .show();
        }

    }


    private void setLink() {
        mLinkPresenter = view.mLink.toLowerCase().trim();
        mLinkPresenterNoChanges = view.mLink.trim();

        int index = mLinkPresenter.length();

        SpannableString linkStyled = new SpannableString(mLinkPresenter);
        linkStyled.setSpan(
                new URLSpan(mLinkPresenter),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        view.mEtChat.setText(mLinkPresenter);

    }


    private Subscription observeTalker() {
        return model.getTalker(view.mTalkerId)
                .subscribe(talker -> {
                    mUserTalker = talker;
                    if (mUserTalker.getThumb() != null){
                        setImage(mUserTalker.getThumb());
                    }
                    else {
                        setImage(mUserTalker.getImageUrl());
                    }

                    view.mTvNameOfTalker.setText(talker.getName());
                    view.mToolbarChat.getMenu().clear();

                    if (!talker.isOnline()){
                        if(talker.getLastSeen() != null) {
                            view.mTvUsername.setVisibility(GONE);
                            String time = GetTimeAgo.getTimeAgo(talker.getLastSeen(), view.mContext);
                            String append = "estava online ";
                            String appendDate = append + time;
                            view.mTvLastSeen.setText(appendDate);
                            view.mTvLastSeen.setVisibility(VISIBLE);
                        }
                        else {
                            view.mTvUsername.setVisibility(GONE);
                            String append = "estava online";
                            view.mTvLastSeen.setText(append);
                            view.mTvLastSeen.setVisibility(VISIBLE);
                        }
                    }
                    else {
                        String append = "online";
                        view.mTvLastSeen.setText(append);
                        view.mTvUsername.setVisibility(GONE);
                        view.mTvLastSeen.setVisibility(VISIBLE);
                    }

                    view.mToolbarChat.inflateMenu(R.menu.menu_chat_user_activity);
                    mMenuItemRemoveTalker = view.mToolbarChat.getMenu().findItem(R.id.action_remove_talkers);
                    mMenuItemProfileTalker = view.mToolbarChat.getMenu().findItem(R.id.action_profile_talker);

                    view.mToolbarChat.setOnMenuItemClickListener(this);
                },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeUserImage(){
        return view.observableUserImage()
                .subscribe(__ -> {
                    model.backToMainActivity(view.mTalkerId);
                },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeBtnSend() {
        return view.observeBtnSend()
                .doOnNext(__ -> view.mFabSend.setEnabled(false))
                .switchMap(__ -> model.getUser())
                .map(__ -> getMessage(mUser))
                .doOnNext(messageUser -> model.sendMessageToFirebase(messageUser, view.mTalkerId))
                .subscribe(__ -> {
                            view.mEtChat.setText("");
                            view.mFabSend.setEnabled(true);
                            view.mLink = null;
                        },
                        Throwable::printStackTrace
                );
    }


    private Subscription populateRecyclerView() {
        return model.getUser()
                .doOnNext(user -> {
                    mUser = user;

                    if(mChatTalkerSwipeAdapter == null) {
                        model.loadData(view.mTalkerId, mChatTalkerAdapter, mMessagesList, view.mRvChat, 30, mCurrentPage,
                                view.mLayoutManager);
                    }
                    else {
                        return;
                    }
                })
                .subscribe(
                        user -> {

                            //view.mRvChat.setVisibility(VISIBLE);
                        },
                        Throwable::printStackTrace);
    }

    private void setAdapter() {

        mChatTalkerAdapter = new ChatTalkerAdapter(view.mContext, mMessagesList, view.mTalkerId, view.mVideoUri,
                view.mVideoPath, view.mImageUri,
                view.mImagePath);

        view.mRvChat.setAdapter(mChatTalkerAdapter);

    }

    private void setAdapterSwipe() {

        mCurrentPage++;

        if (mChatTalkerAdapter != null && mMessagesList != null) {
            mMessagesList.clear();
            mChatTalkerAdapter.notifyDataSetChanged();
            mMessagesList = null;
            mChatTalkerAdapter = null;

            if (mChatTalkerSwipeAdapter == null && mMessagesSwipeList == null){
                mMessagesSwipeList = new ArrayList<>();
                mChatTalkerSwipeAdapter = new ChatTalkerAdapter(view.mContext, mMessagesSwipeList, view.mTalkerId, view.mVideoUri,
                        view.mVideoPath, view.mImageUri,
                        view.mImagePath);
            }

        }

        if(mChatTalkerSwipeAdapter != null && mMessagesSwipeList != null){

            mMessagesSwipeList.clear();
            mChatTalkerSwipeAdapter.notifyDataSetChanged();
            mMessagesSwipeList = null;
            mChatTalkerSwipeAdapter = null;

            mMessagesSwipeList = new ArrayList<>();
            mChatTalkerSwipeAdapter = new ChatTalkerAdapter(view.mContext, mMessagesSwipeList, view.mTalkerId, view.mVideoUri,
                    view.mVideoPath, view.mImageUri,
                    view.mImagePath);

            view.mRvChat.setAdapter(mChatTalkerSwipeAdapter);


            model.loadMoreItems(view.mTalkerId, mChatTalkerSwipeAdapter, mMessagesSwipeList, view.mSwipeLayout, 30, mCurrentPage,
                    view.mLayoutManager, view.mRvChat);


        }
        else {
            mMessagesSwipeList = new ArrayList<>();
            mChatTalkerSwipeAdapter = new ChatTalkerAdapter(view.mContext, mMessagesSwipeList, view.mTalkerId, view.mVideoUri,
                    view.mVideoPath, view.mImageUri,
                    view.mImagePath);

            view.mRvChat.setAdapter(mChatTalkerSwipeAdapter);


            model.loadMoreItems(view.mTalkerId, mChatTalkerSwipeAdapter, mMessagesSwipeList, view.mSwipeLayout, 30, mCurrentPage,
                    view.mLayoutManager, view.mRvChat);

        }
    }


    private Subscription observeIbCamera() {
        return view.observeIbCamera()
                /*.filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })*/
                .map(__ -> model.getTalker(view.mTalkerId))
                .subscribe(__ -> {
                    Intent intent = new Intent(view.mContext, CamTalkerActivity.class);
                    intent.putExtra(CONTACT_ID, view.mTalkerId);
                    view.mContext.startActivity(intent);
                },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeToolbarClicks() {
        return view.observeToolbarClicks()
                .subscribe(__ -> {
                            if (view.fromNotification != null){
                                Intent intent = new Intent(view.mContext, DetailTalkerActivity.class);
                                intent.putExtra(UPDATE_LAST_MESSAGE, "true");
                                intent.putExtra(CONTACT_ID, view.mTalkerId);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            }
                            else {
                                model.openDetailTalker(view.mTalkerId);

                            }

                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeBtnArrowBack(){
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                    if (view.fromNotification != null){
                        Intent intent = new Intent(view.mContext, MainActivity.class);
                        intent.putExtra(UPDATE_LAST_MESSAGE, "true");
                        intent.putExtra(CONTACT_ID, view.mTalkerId);
                        view.mContext.startActivity(intent);
                        view.mContext.finish();

                    }
                    else {
                        model.backToMainActivity(view.mTalkerId);
                    }

                },
                        Throwable::printStackTrace
                );
    }

    public void backToMainActivity(){
        model.backToMainActivity(view.mTalkerId);
    }

    //VOICE MESSAGE
    //Observable to change the button
    /*private Subscription observeETChat() {
        return view.observeETChat()
                .subscribe(__ -> {
                    if(view.mEtChat.getText().toString().trim().length() >= 1){
                        view.mIvVoice.setVisibility(GONE);
                        view.mIbCamera.setVisibility(GONE);
                        view.mIbShare.setVisibility(GONE);
                        view.mBtnSend.setVisibility(VISIBLE);
                        view.mBtnSend.setEnabled(true);
                    }
                    else {
                        view.mBtnVoiceRecord.setVisibility(VISIBLE);
                        view.mBtnSend.setVisibility(GONE);
                        view.mIbCamera.setVisibility(VISIBLE);
                        view.mIbShare.setVisibility(VISIBLE);
                    }
                },
                        Throwable::printStackTrace
                );
    }*/

    //Observable to get the first click on the button(microphone) to request permission to use microphone
    private Subscription observeClickBtnVoiceRecord() {
        return view.observeClickBtnVoiceRecord()
                .filter(__ -> {
                    if(!ShowSnackBarInfoInternet.checkConnectionAnother()){
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    }
                    else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .doOnNext(__ -> model.verifyStoragePermissions())
                .filter(__ -> true)
                .doOnNext(__ -> openVoiceRecord())
                .subscribe();
    }

    //Method to open voice record
    private void openVoiceRecord() {
        view.mIvVoice.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                File audioFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                File mAudioFolder = new File(audioFile, "Tribus Áudio");

                if (!mAudioFolder.exists()) {
                    mAudioFolder.mkdirs();
                }

                File videoFile = null;
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String prepend = "AUDIO_" + timestamp + "_";
                try {
                    videoFile = File.createTempFile(prepend, ".3gp", mAudioFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mFileName = videoFile.getAbsolutePath();
                startRecording();

            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                stopRecording();

            }
            return false;
        });
    }

    //Method to start recording
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e("Valor", "prepare() failed");
        }

    }

    //Method to stop recording
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        model.uploadAudioToFirebase(mFileName, uniqueID);

    }


    private Subscription observeIbShare() {
        return view.observeIbShare()
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                .doOnNext(__ -> showDialogToShare()) //SHOW DIALOG FI STRAIGHT IF THERE IS PERMISSION ALREADY
                .subscribe();
    }

    private Subscription observeIvLocation(){
        return view.observeIbLocation()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar localização ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }

    private Subscription observeIvDocs(){
        return view.observeIbDocs()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar documentos ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }



    private Subscription observeIvContacts(){
        return view.observeIbContacts()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar contatos ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }



    //SETTING VIEWS
    //mLargeUserImage
    private void setImage(String userImageUrl) {
        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                //Log.d("Valor: ", "onFailure - View: " + throwable.toString());

            }

            @Override
            public void onIntermediateImageFailed(String id, Throwable throwable) {
                super.onIntermediateImageFailed(id, throwable);
            }

            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
            }

            @Override
            public void onIntermediateImageSet(String id, Object imageInfo) {
                super.onIntermediateImageSet(id, imageInfo);
            }

            @Override
            public void onRelease(String id) {
                super.onRelease(id);
            }

            @Override
            public void onSubmit(String id, Object callerContext) {
                super.onSubmit(id, callerContext);
                //Log.d("Valor: ", "onSubmit");

            }
        };
        DraweeController dc = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(userImageUrl))
                .setControllerListener(listener)
                .setOldController(view.mCircleUserImage.getController())
                .build();
        view.mCircleUserImage.setController(dc);

    }


    //Create text message
    private MessageUser getMessage(User user) {
        String messageLink = view.mEtChat.getText().toString().toLowerCase().trim();
        String etMessageLink = view.mEtChat.getText().toString().trim();

        if (messageLink.contains("http://")
                || (messageLink.contains("https://")
                || (messageLink.contains("www.")))) {

            if (view.mLink != null && messageLink.contains(mLinkPresenter)) {


                int matchStart = 0;
                int matchEnd = 0;
                Matcher matcher = FindUrl.urlPattern.matcher(messageLink);
                while (matcher.find()) {
                    matchStart = matcher.start(1);
                    matchEnd = matcher.end();
                    // now you have the offsets of a URL match
                }

                String newLink = etMessageLink.substring(matchStart, matchEnd);

                return new MessageUser(view.mEtChat.getText().toString().trim(), newLink,
                        LINK, user.getId(), user.isAccepted());


            } else {
                return new MessageUser(view.mEtChat.getText().toString().trim(), mLinkPresenterNoChanges,
                        LINK, user.getId(), user.isAccepted());

            }
        } else {
            return new MessageUser(view.mEtChat.getText().toString().trim(),
                    TEXT, user.getId(), user.isAccepted());
        }

    }

    /*private MessageUser getMessage(User user) {
        String messageLink = view.mEtChat.getText().toString().toLowerCase().trim();
        String etMessageLink = view.mEtChat.getText().toString().trim();

        if(view.mLink != null && messageLink.toLowerCase().contains(mLinkPresenter)){
            return new MessageUser(view.mEtChat.getText().toString().trim(), mLinkPresenter,
                    "LINK_INTO_MESSAGE", user.getId(), user.isAccepted());
        }
        else if(messageLink.contains("http://")
                || (messageLink.contains("https://")
                || (messageLink.contains("www.")))){

            int matchStart = 0;
            int matchEnd = 0;
            Matcher matcher = FindUrl.urlPattern.matcher(messageLink);
            while (matcher.find()) {
                matchStart = matcher.start(1);
                matchEnd = matcher.end();
                // now you have the offsets of a URL match
            }

            String newLink = etMessageLink.substring(matchStart, matchEnd);

            return new MessageUser(view.mEtChat.getText().toString().trim(), newLink,
                    "LINK_INTO_MESSAGE", user.getId(), user.isAccepted());

        }
        else {
            return new MessageUser(view.mEtChat.getText().toString().trim(),
                    "TEXT", user.getId(), user.isAccepted());
        }

    }*/


    //SET NEW IMAGE
    public void showDialogToShare() {

        //CREATE DIALOG TO SHOW NEW IMAGE
        //CONFIGURATION OF DIALOG
        AlertDialog.Builder builder = new AlertDialog.Builder(view.mContext, R.style.MyDialogShareTheme);
        LayoutInflater inflater = view.mContext.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_btn_share, null);
        ImageView mBtnShareImage = dialogView.findViewById(R.id.btn_share_image);
        ImageView mBtnShareVideo = dialogView.findViewById(R.id.btn_share_video);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow()
                .getAttributes();
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmlp.gravity = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        dialog.getWindow().setGravity(wmlp.gravity);

        dialog.show();

        //CLICK LISTENER TO BUTTON
        mBtnShareImage.setOnClickListener(v -> {
            model.getImage();
            dialog.dismiss();
        });

        //CLICK LISTENER TO BUTTON
        mBtnShareVideo.setOnClickListener(v -> {
            model.getVideo();
            dialog.dismiss();
        });

    }


    //GET IMAGE AFTER GRANTED PERMISSION
    public void getImage() {
        model.getImage();
    }

    //SEND IMAGE TO SEND IMAGE ACTIVITY
    public void sendImageToSendImageActivity(Uri uri, int fileSize, String mTalkerId) {
        model.openActivitySendImage(uri, fileSize, mTalkerId);
    }

    //SEND IMAGE TO SEND IMAGE ACTIVITY
    public void sendVideoToSendVideoActivity(Uri uri, int fileSize, String mTalkerId) {
        model.openActivitySendVideo(uri, fileSize, mTalkerId);
    }



    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == mMenuItemRemoveTalker.getItemId()) {

            if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
            } else {
                if (mUserTalker != null){
                    model.openBlockUser(mUserTalker);
                }
            }
        }

        if (id == mMenuItemProfileTalker.getItemId()) {
            if (mUserTalker != null) {
                model.openDetailTalker(mUserTalker.getId());
            }

        }

        return true;
    }

    public void onDestroy() {
        subscription.clear();
        model.removeListeners();
    }
}
