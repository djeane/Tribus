package apptribus.com.tribus.activities.chat_tribu.mvp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
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

import static apptribus.com.tribus.util.Constantes.FROM_NOTIFICATION;
import static apptribus.com.tribus.util.Constantes.LINK_INTO_MESSAGE;
import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TOPIC_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBU_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_NAME;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

@SuppressLint("ViewConstructor")
public class ChatTribuView extends FrameLayout {

    @BindView(R.id.relative_chat_tribu)
    RelativeLayout mRelativeChatTribu;

    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBar;

    @BindView(R.id.toolbar_chat_tribu)
    Toolbar mToolbarChat;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.tv_topic)
    TextView mTvTopic;

    @BindView(R.id.tv_name_tribu)
    TextView mTvTribuName;

    @BindView(R.id.rv_chat)
    public RecyclerView mRvChat;

    @BindView(R.id.constraint_edit)
    ConstraintLayout mConstraintEdit;

    @BindView(R.id.et_chat)
    EmojiconEditText mEtChat;

    @BindView(R.id.fab_send)
    FloatingActionButton mFabSend;

    @BindView(R.id.flex_icons_chat)
    FlexboxLayout mFlexIconsChat;

    @BindView(R.id.iv_docs)
    ImageView mIvDocs;

    //@BindView(R.id.iv_localization)
    //ImageView mIvLocation;

    //@BindView(R.id.iv_contacts)
    //ImageView mIvContacts;

    @BindView(R.id.iv_voice)
    ImageView mIvVoice;

    @BindView(R.id.iv_camera)
    ImageView mIvCamera;

    @BindView(R.id.iv_gallery)
    ImageView mIvGallery;

    //TO REPLY MESSAGE
    @BindView(R.id.relative_reply_message)
    RelativeLayout mRelativeReplyMessage;

    @BindView(R.id.tv_reply_username)
    TextView mTvReplyUsername;

    @BindView(R.id.tv_reply_message)
    TextView mTvReplyMessage;

    @BindView(R.id.iv_close_reply)
    ImageView mIvCloseReplay;

    @BindView(R.id.progressbar_top)
    public ProgressBar mProgressBarTop;

    public String mTribuUniqueName;
    public String mTribuKey;
    public String mTopicKey;
    public String mTopicName;
    public String mTribuName;
    public LinearLayoutManager mLayoutManager;

    //TO SAVE AND SHOW VIDEO
    public Uri mVideoUri;
    public String mVideoPath;

    //TO SAVE AND SHOW VIDEO
    public Uri mImageUri;
    public String mImagePath;
    public AppCompatActivity mContext;

    public String fromNotification;
    public String mLink;

    public ChatTribuView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_chat_tribu, this);

        ButterKnife.bind(this);

        mTribuUniqueName = activity.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);
        mTopicKey = activity.getIntent().getStringExtra(TOPIC_KEY);
        mTribuKey = activity.getIntent().getStringExtra(TRIBU_KEY);
        mTopicName = activity.getIntent().getStringExtra(TOPIC_NAME);
        mTribuName = activity.getIntent().getStringExtra(TRIBU_NAME);

        fromNotification = activity.getIntent().getStringExtra(FROM_NOTIFICATION);

        mLink = activity.getIntent().getStringExtra(LINK_INTO_MESSAGE);

        //TO SAVE AND SHOW VIDEO
        mVideoUri = activity.getIntent().getData();
        mVideoPath = activity.getIntent().getExtras().getString("video_path");

        //TO SAVE AND SHOW IMAGE
        mImageUri = activity.getIntent().getData();
        mImagePath = activity.getIntent().getExtras().getString("image_path");

        //SET RECYCLER VIEW
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.setReverseLayout(true);
        mRvChat.setLayoutManager(mLayoutManager);
        setOverflowButtonColor(activity, getResources().getColor(R.color.colorIcons));
    }



    public static void setOverflowButtonColor(final AppCompatActivity activity, final int color) {
        //final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final String overflowDescription = activity.getString(R.string.arrow_back);
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

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
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


    public Observable<Void> observeIbDocs(){
        return RxView.clicks(mIvDocs);
    }

    /*public Observable<Void> observeIbLocation(){
        return RxView.clicks(mIvLocation);
    }

    public Observable<Void> observeIbContacts(){
        return RxView.clicks(mIvContacts);
    }*/



    public Observable<Void> observeToolbarClicks(){
        return RxView.clicks(mToolbarChat);
    }

    public Observable<Void> observeIvCloseReplay(){
        return RxView.clicks(mIvCloseReplay);
    }
}
