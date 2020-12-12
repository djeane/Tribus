package apptribus.com.tribus.activities.chat_user.mvp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.flexbox.FlexboxLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.ArrayList;

import apptribus.com.tribus.R;
import apptribus.com.tribus.util.NpaLinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

/**
 * Created by User on 6/27/2017.
 */

public class ChatUserView extends FrameLayout {

    @BindView(R.id.toolbar_chat_talker)
    Toolbar mToolbarChat;

    @BindView(R.id.circle_talker_image)
    SimpleDraweeView mCircleUserImage;

    @BindView(R.id.rv_chat_talker)
    RecyclerView mRvChat;
    //RecyclerView mRvChat;

    @BindView(R.id.et_chat)
    EmojiconEditText mEtChat;

    @BindView(R.id.tv_name_talker)
    TextView mTvNameOfTalker;

    @BindView(R.id.tv_username)
    TextView mTvUsername;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.progress_chat)
    ProgressBar mProgress;

    @BindView(R.id.swipe_chat)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.tv_last_seen)
    TextView mTvLastSeen;

    @BindView(R.id.relative_chat_user)
    RelativeLayout mRelativeChatUser;

    @BindView(R.id.constraint_edit)
    ConstraintLayout mConstraintEdit;

    @BindView(R.id.fab_send)
    FloatingActionButton mFabSend;

    @BindView(R.id.flex_icons_chat)
    FlexboxLayout mFlexIconsChat;

    @BindView(R.id.iv_docs)
    ImageView mIvDocs;

    @BindView(R.id.iv_localization)
    ImageView mIvLocation;

    @BindView(R.id.iv_contacts)
    ImageView mIvContacts;

    @BindView(R.id.iv_voice)
    ImageView mIvVoice;

    @BindView(R.id.iv_camera)
    ImageView mIvCamera;

    @BindView(R.id.iv_gallery)
    ImageView mIvGallery;


    public String mTalkerId;
    public NpaLinearLayoutManager mLayoutManager;

    //TO SAVE AND SHOW VIDEO
    public Uri mVideoUri;
    public String mVideoPath;

    //TO SAVE AND SHOW VIDEO
    public Uri mImageUri;
    public String mImagePath;
    public AppCompatActivity mContext;

    public String fromNotification;
    public String mLink;

    //public EmojIconActions emojIcon;
    //public ImageView mIvEmoticon;


    public ChatUserView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_chat_user, this);

        //mIvEmoticon = activity.findViewById(R.id.ib_emoticon);

        ButterKnife.bind(this);



        //emojIcon = new EmojIconActions(activity, mRelativeChatUser, mEtChat, mIvEmoticon);

        mTalkerId = activity.getIntent().getExtras().getString(CONTACT_ID);
        fromNotification = activity.getIntent().getStringExtra("fromNotification");
        mLink = activity.getIntent().getStringExtra(LINK_INTO_MESSAGE);


        //TO SAVE AND SHOW VIDEO
        mVideoUri = activity.getIntent().getData();
        mVideoPath = activity.getIntent().getExtras().getString("video_path");

        //TO SAVE AND SHOW IMAGE
        mImageUri = activity.getIntent().getData();
        mImagePath = activity.getIntent().getExtras().getString("image_path");

        //mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true);
        //mLayoutManager.setStackFromEnd(true);
        mLayoutManager = new NpaLinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mLayoutManager.setStackFromEnd(true);
        mRvChat.setLayoutManager(mLayoutManager);
        setOverflowButtonColor(activity, getResources().getColor(R.color.colorIcons));
    }

    public static void setOverflowButtonColor(final AppCompatActivity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            final ArrayList<View> outViews = new ArrayList<>();
            decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
            if (outViews.isEmpty()) {
                return;
            }
            AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
            overflow.setColorFilter(color);
        });
    }


    public Observable<Void> observeClickBtnVoiceRecord(){
        return RxView.clicks(mIvVoice);
    }

    public Observable<CharSequence> observeETChat(){
        return RxTextView.textChanges(mEtChat);
    }

    public Observable<Void> observeBtnSend(){
        return RxView.clicks(mFabSend);
    }

    public Observable<Void> observeIbCamera(){
        return RxView.clicks(mIvCamera);
    }

    public Observable<Void> observeIbShare(){
        return RxView.clicks(mIvGallery);
    }

    public Observable<Void> observeIbLocation(){
        return RxView.clicks(mIvLocation);
    }

    public Observable<Void> observeIbDocs(){
        return RxView.clicks(mIvDocs);
    }

    public Observable<Void> observeIbContacts(){
        return RxView.clicks(mIvContacts);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

    public Observable<Void> observableUserImage(){
        return RxView.clicks(mCircleUserImage);
    }


    public Observable<Void> observeToolbarClicks(){
        return RxView.clicks(mToolbarChat);
    }

}
