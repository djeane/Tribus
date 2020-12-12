package apptribus.com.tribus.activities.chat_tribu.mvp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.chat_tribu.adapter.ChatTribuAdapter;
import apptribus.com.tribus.activities.chat_tribu.camera.CamActivity;
import apptribus.com.tribus.activities.chat_tribu.view_holder.user_right.TextMessageChatTribuUserRightVH;
import apptribus.com.tribus.activities.main_activity.MainActivity;
import apptribus.com.tribus.pojo.ConversationTopic;
import apptribus.com.tribus.pojo.MessageUser;
import apptribus.com.tribus.pojo.Tribu;
import apptribus.com.tribus.pojo.User;
import apptribus.com.tribus.util.FindUrl;
import apptribus.com.tribus.util.PresenceSystemAndLastSeen;
import apptribus.com.tribus.util.ShowSnackBarInfoInternet;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static android.view.View.VISIBLE;
import static apptribus.com.tribus.util.Constantes.FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.GENERAL_TRIBUS;
import static apptribus.com.tribus.util.Constantes.GENERAL_USERS;
import static apptribus.com.tribus.util.Constantes.LINK;
import static apptribus.com.tribus.util.Constantes.MESSAGE_REPLY;
import static apptribus.com.tribus.util.Constantes.TEXT;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBUS_FOLLOWERS;
import static apptribus.com.tribus.util.Constantes.TRIBUS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.TRIBUS_TOPICS;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;
import static apptribus.com.tribus.util.Constantes.USERS_MESSAGES;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_INVITATIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKERS_PERMISSIONS;
import static apptribus.com.tribus.util.Constantes.USERS_TALKS;

public class ChatTribuPresenter /*implements Toolbar.OnMenuItemClickListener */ implements TextMessageChatTribuUserRightVH.TextMessageChatTribuUserRightListener {

    private final ChatTribuView view;
    private final ChatTribuModel model;
    private CompositeSubscription subscription = new CompositeSubscription();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private boolean response;
    private String message;
    private String mFileName = null;
    private MediaRecorder mRecorder;
    private MenuItem mAddFollowers;
    private MenuItem mLeaveTribu;
    private MenuItem mChangeAdmin;
    public MenuItem mProfileTribuItem;
    public Tribu mTribu;
    public ConversationTopic mTopic;
    private String uniqueID;
    public static boolean isOpen;
    private File videoFile = null;
    private User mUser;
    private String mLinkPresenter;
    private String mLinkPresenterNoChanges;
    private boolean comingFromOnRestart = false;
    private String mReplyUserId;
    private String mReplyMessage;
    private String mReplyMessageKey;
    private Tribu mTribuAdapter;

    //ADAPTER
    private List<MessageUser> mMessagesList = new ArrayList<>();
    private List<MessageUser> mMessagesListSwipe = new ArrayList<>();
    private List<MessageUser> mCurrentUserMessagesList = new ArrayList<>();
    private ChatTribuAdapter mChatTribuAdapter;
    private ChatTribuAdapter mChatTribuAdapterSwipe;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;


    //NEW PAGINTATION WITH FIRESTORE
    private List<MessageUser> mNewMessagesList;
    private List<MessageUser> mNewMessagesListLoading;


    //REFERENCES - FIREBASE(KEEP SYNCED)
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReferenceTribu = mDatabase.getReference().child(GENERAL_TRIBUS);
    private DatabaseReference mReferenceUser = mDatabase.getReference().child(GENERAL_USERS);
    private DatabaseReference mReferenceTribusFollowers = mDatabase.getReference().child(TRIBUS_FOLLOWERS);
    private DatabaseReference mReferenceFollowers = mDatabase.getReference().child(FOLLOWERS);
    private DatabaseReference mRefTribusMessage = mDatabase.getReference().child(TRIBUS_MESSAGES);
    private DatabaseReference mRefUsersMessage = mDatabase.getReference().child(USERS_MESSAGES);
    private DatabaseReference mRefUsersTalk = mDatabase.getReference().child(USERS_TALKS);
    private DatabaseReference mRefUsersTalkersPermissions = mDatabase.getReference().child(USERS_TALKERS_PERMISSIONS);
    private DatabaseReference mRefUsersTalkersInvitations = mDatabase.getReference().child(USERS_TALKERS_INVITATIONS);
    private DatabaseReference mRefTribusTopics = mDatabase.getReference().child(TRIBUS_TOPICS);
    //private String mTopicKey = mRefTribusTopics.push().getKey();


    public ChatTribuPresenter(ChatTribuView view, ChatTribuModel model) {
        this.view = view;
        this.model = model;
    }

    public void onStart() {

        //KEEP SYNCED
        mReferenceTribu.keepSynced(true);
        mReferenceUser.keepSynced(true);
        mReferenceTribusFollowers.keepSynced(true);
        mReferenceFollowers.keepSynced(true);
        mRefTribusMessage.keepSynced(true);
        mRefUsersMessage.keepSynced(true);
        mRefUsersTalk.keepSynced(true);
        mRefUsersTalkersPermissions.keepSynced(true);
        mRefUsersTalkersInvitations.keepSynced(true);
        mRefTribusTopics.keepSynced(true);

        PresenceSystemAndLastSeen.presenceSystem();


        if (view.mTopicKey != null || !view.mTopicKey.equals("")) {
            FirebaseMessaging.getInstance().subscribeToTopic(view.mTopicKey);
        }

        setToolbarTitles();
        subscription.add(observeUser());
        subscription.add(loadMessages());
        //loadMessages();
        loadMoreMessages();


        subscription.add(observeBtnSend());
        subscription.add(observeIvCloseReplay());
        subscription.add(observeIbShare());
        subscription.add(observeBtnArrowBack());
        subscription.add(observeIvDocs());

        if (view.mLink != null && !view.mLink.equals("")) {
            setLink();
        }

        isOpen = true;

        NotificationManager notificationManager = (NotificationManager) view.mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(4);
        notificationManager.cancel(6);

    }

    private void setToolbarTitles() {

        if (view.mTribuName != null && view.mTribuUniqueName != null) {

            String appendNameAndUsername = view.mTribuName + " (" + view.mTribuUniqueName + ")";
            //String[] firstName = view.mTribuName.split(" ");
            //String appendNameAndUsername = firstName[0] + " (" + view.mTribuUniqueName + ")";
            view.mTvTribuName.setText(appendNameAndUsername.trim());

        }

        if (view.mTopicName != null){
            view.mTvTopic.setText(view.mTopicName.trim());
        }

    }

    public void onResume() {

        PresenceSystemAndLastSeen.presenceSystem();
    }


    public void onPause() {
        PresenceSystemAndLastSeen.lastSeen();

    }

    public void onStop() {
        isOpen = false;
    }

    public void onRestart() {
        isOpen = true;

    }

    private void setLink() {
        mLinkPresenter = view.mLink.toLowerCase().trim();
        mLinkPresenterNoChanges = view.mLink.trim();

        int index = mLinkPresenter.length();

        SpannableString linkStyled = new SpannableString(mLinkPresenterNoChanges);
        linkStyled.setSpan(
                new URLSpan(mLinkPresenterNoChanges),
                0,
                index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        view.mEtChat.setText(linkStyled);
    }

    private Subscription observeUser() {
        return model.getUser()
                .subscribe(user -> {
                            mUser = user;
                        },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeBtnSend() {
        return view.observeBtnSend()
                .filter(isEmpty -> {
                    if (validateEtChat()) {
                        view.mFabSend.setEnabled(false);
                        return true;
                    } else {
                        return false;
                    }
                })
                .map(__ -> {
                    if (mUser != null) {
                        return getMessage(mUser);
                    } else {
                        Toast.makeText(view.mContext, "Usuário não encontrado para enviar mensagem.", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                })
                .doOnNext(newMessage -> {
                    if (newMessage != null) {
                        model.sendMessageToFirestore(newMessage, view.mTopicKey, view.mTribuKey);
                    } else {
                        Toast.makeText(view.mContext, "Sua mensaage não está correta.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .subscribe(__ -> {
                            view.mRelativeReplyMessage.setVisibility(View.GONE);
                            view.mEtChat.setText("");
                            view.mFabSend.setEnabled(true);
                            view.mLink = null;
                        },
                        Throwable::printStackTrace
                );

    }

    private boolean validateEtChat() {
        return !TextUtils.isEmpty(view.mEtChat.getText().toString());
    }

    /*private void loadMessages() {
        mMessagesList.clear();
        model.loadMessages(mMessagesList, view, this);
    }*/

    private Subscription loadMessages() {
        mMessagesList.clear();
        return model.getTribu(view.mTribuKey)
                .concatMap(tribu -> {
                    mTribuAdapter = tribu;
                    return model.loadMessages(mMessagesList, view, mChatTribuAdapter);
                })
                .subscribe(messageUserList -> {
                    if (messageUserList != null){
                        mMessagesList = messageUserList;

                        mChatTribuAdapter = new ChatTribuAdapter(view.mContext,
                                mMessagesList, view.mTribuKey, view.mVideoUri, view.mVideoPath,
                                view.mImageUri, view.mImagePath, view, view.mTopicKey, this,
                                mTribuAdapter.getProfile().isPublic());

                        view.mRvChat.setAdapter(mChatTribuAdapter);
                    }

                });
    }

    private void loadMoreMessages() {

        view.mRvChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Boolean reachedTop = !recyclerView.canScrollVertically(-1);

                if (reachedTop) {
                    view.mProgressBarTop.bringToFront();
                    view.mProgressBarTop.setVisibility(View.VISIBLE);


                    model.loadMoreMessages(mMessagesList, view, mChatTribuAdapter);
                }
            }

        });

    }

    /*private void loadMoreMessages() {
        model.loadMoreMessages(mMessagesList, view);
    }*/


    private Subscription observeToolbarClicks() {
        return view.observeToolbarClicks()
                .subscribe(__ -> {
                            if (mTribu.getAdmin().getUidAdmin().equals(mAuth.getCurrentUser().getUid())) {
                                model.openProfileTribuAdminActivity(mTribu);
                            } else {
                                model.openDetailTribu(mTribu);
                            }

                        },
                        Throwable::printStackTrace
                );
    }


    private Subscription observeIbCamera() {
        return view.observeIbCamera()
                .map(__ -> model.getTribu(view.mTribuKey))
                .subscribe(__ -> {
                            Intent intent = new Intent(view.getContext(), CamActivity.class);
                            intent.putExtra(TRIBU_UNIQUE_NAME, view.mTribuUniqueName);
                            intent.putExtra(TOPIC_KEY, view.mTopicKey);
                            view.getContext().startActivity(intent);
                        },
                        Throwable::printStackTrace
                );
    }

    private Subscription observeIbShare() {
        return view.observeIbShare()
                .doOnNext(__ -> model.requirePermission())
                .filter(__ -> true)
                .doOnNext(__ -> showDialogToShare()) //SHOW DIALOG FI STRAIGHT IF THERE IS PERMISSION ALREADY
                .subscribe();
    }

    private Subscription observeBtnArrowBack() {
        return view.observeBtnArrowBack()
                .subscribe(__ -> {
                            if (view.fromNotification != null) {
                                Intent intent = new Intent(view.mContext, MainActivity.class);
                                view.mContext.startActivity(intent);
                                view.mContext.finish();

                            } else {
                                model.backToMainActivity();
                            }

                        },
                        Throwable::printStackTrace
                );
    }


    //VOICE MESSAGE
    //Observable to change the button
    private Subscription observeETChat() {
        return view.observeETChat()
                .subscribe(__ -> {
                            /*if (view.mEtChat.getText().toString().trim().length() >= 1) {
                                view.mBtnVoiceRecord.setVisibility(GONE);
                                view.mIbCamera.setVisibility(GONE);
                                view.mIbShare.setVisibility(GONE);
                                view.mBtnSend.setVisibility(VISIBLE);
                                view.mBtnSend.setEnabled(true);
                            } else {
                                view.mBtnVoiceRecord.setVisibility(VISIBLE);
                                view.mBtnSend.setVisibility(GONE);
                                view.mIbCamera.setVisibility(VISIBLE);
                                view.mIbShare.setVisibility(VISIBLE);
                            }*/
                        },
                        Throwable::printStackTrace
                );
    }

    //Observable to get the first click on the button(microphone) to request permission to use microphone
    private Subscription observeClickBtnVoiceRecord() {
        return view.observeClickBtnVoiceRecord()
                .filter(__ -> {
                    if (!ShowSnackBarInfoInternet.checkConnectionAnother()) {
                        ShowSnackBarInfoInternet.showToastInfoInternet(view.mContext);
                        return false;
                    } else {
                        ShowSnackBarInfoInternet.showSnack(true, view);
                        return true;
                    }
                })
                .doOnNext(__ -> model.verifyStoragePermissions())
                .filter(__ -> true)
                .doOnNext(__ -> openVoiceRecord())
                .subscribe();
    }

    private Subscription observeIvDocs() {
        return view.observeIbDocs()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar documentos ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }


    /*private Subscription observeIvLocation() {
        return view.observeIbLocation()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar localização ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }


    private Subscription observeIvContacts() {
        return view.observeIbContacts()
                .subscribe(__ -> {
                    Toast.makeText(view.mContext, "Compartilhar contatos ainda está indisponível. Por favor, aguarde!", Toast.LENGTH_SHORT)
                            .show();
                });
    }*/

    //Method to open voice record
    private void openVoiceRecord() {

        view.mIvVoice.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                File audioFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                File mAudioFolder = new File(audioFile, "Tribus-Audio");

                if (!mAudioFolder.exists()) {
                    mAudioFolder.mkdirs();
                }

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String prepend = "AUDIO_" + timestamp + "_";
                try {
                    videoFile = File.createTempFile(prepend, ".3gp", mAudioFolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecording();


            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                stopRecording();

            }
            return false;
        });

    }


    //Method to start recording
    private void startRecording() {

        mFileName = videoFile.getAbsolutePath();

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //Method to stop recording
    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        model.uploadAudioToFirebase(mFileName, uniqueID, view.mTopicKey);

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
                        LINK, user.getId(), user.isAccepted(), view.mTopicKey);


            } else {
                return new MessageUser(view.mEtChat.getText().toString().trim(), mLinkPresenterNoChanges,
                        LINK, user.getId(), user.isAccepted(), view.mTopicKey);
            }

        } else {

            if (view.mRelativeReplyMessage.getVisibility() == VISIBLE){
                return new MessageUser(view.mEtChat.getText().toString().trim(),
                        MESSAGE_REPLY, user.getId(), user.isAccepted(), view.mTopicKey, mReplyUserId,
                        mReplyMessage, mReplyMessageKey);

            }
            else {
                return new MessageUser(view.mEtChat.getText().toString().trim(),
                        TEXT, user.isAccepted(), user.getId(), view.mTopicKey);

            }
        }

    }


    //SET NEW IMAGE
    private void showDialogToShare() {

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
    public void sendImageToSendImageActivity(Uri uri, int fileSize, String mTribusKey, String mTopicKey) {
        model.openActivitySendImage(uri, fileSize, mTribusKey, mTopicKey);
    }

    //SEND IMAGE TO SEND IMAGE ACTIVITY
    public void sendVideoToSendVideoActivity(Uri uri, int fileSize, String mTribusKey, String mTopicKey) {
        model.openActivitySendVideo(uri, fileSize, mTribusKey, mTopicKey);
    }

    public void onDestroy() {
        subscription.clear();
    }

    @Override
    public void onReplyMessageText(MessageUser message) {
        subscription.add(observeReplyMessageText(message));
    }

    @Override
    public void openDetailUserActivity(String contactId, String userId, String tribuKey) {
        model.openDetailUserActivity(contactId, userId, tribuKey);
    }

    private Subscription observeReplyMessageText(MessageUser message) {
        return model.getReplyMessageUser(message)
                .subscribe(user -> {
                            mReplyMessage = message.getMessage();
                            mReplyMessageKey = message.getKey();
                            mReplyUserId = user.getId();
                            String[] firstName = user.getName().split(" ");
                            String appendReplyNameAndUsername = firstName[0] + " (" + user.getUsername() + ")";
                            view.mTvReplyUsername.setText(appendReplyNameAndUsername);
                            view.mTvReplyMessage.setText(message.getMessage());
                            view.mRelativeReplyMessage.setVisibility(VISIBLE);
                            view.mEtChat.setHint("Sua resposta...");
                        },
                        Throwable::printStackTrace);
    }

    private Subscription observeIvCloseReplay(){
        return view.observeIvCloseReplay()
                .subscribe(__ -> {
                            view.mRelativeReplyMessage.setVisibility(View.GONE);
                            view.mTvReplyUsername.setText("");
                            view.mTvReplyMessage.setText("");
                            view.mEtChat.setText("");
                            view.mEtChat.setHint("Sua mensagem...");
                        },
                        Throwable::printStackTrace
                );
    }

}
