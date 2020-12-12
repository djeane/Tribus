package apptribus.com.tribus.activities.show_video_talker.mvp;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Formatter;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.send_video.interfaces.ComponentListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.MESSAGE_REFERENCE;
import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

/**
 * Created by User on 8/16/2017.
 */

public class ShowVideoTalkerView extends FrameLayout {

    @BindView(R.id.parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.linear_video)
    LinearLayout mLinearVideo;

    @BindView(R.id.video_frame)
    SimpleExoPlayerView mSimpleExoPlayerView;

    @BindView(R.id.root)
    RelativeLayout mRelativeRoot;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.toolbar_show_video_talker)
    Toolbar mToolbarShowVideo;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.circle_talker_image)
    SimpleDraweeView mCircleTalkerImage;

    @BindView(R.id.tv_name_talker)
    TextView mTalkerName;

    @BindView(R.id.tv_username_talker)
    TextView mUsenameTalker;

    @BindView(R.id.middle)
    RelativeLayout mRelativeMiddle;

    @BindView(R.id.btn_play)
    ImageButton mBtnPlay;

    @BindView(R.id.btn_pause)
    ImageButton mBtnPause;

    @BindView(R.id.seekbar_time)
    RelativeLayout mRelativeSeekBarTime;

    @BindView(R.id.txt_currentTime)
    TextView mCurrentTime;

    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.txt_totalDuration)
    TextView mTotalDuration;

    @BindView(R.id.card_description)
    CardView mCardShowDescription;

    @BindView(R.id.tv_video_description)
    TextView mTvDescription;

    @BindView(R.id.card_buttons)
    CardView mCardButtons;

    public String mTalkerKey;
    public String mMessageReference;

    //ANOTHER IMPLMENTATION - https://github.com/Foso/ExoPlayer-with-MediaControls/blob/master/Exoplayer/app/src/main/java/jensklingenberg/de/exampleexoplayer/MainActivity.java
    public static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public ComponentListener mComponentListenter;
    public SimpleExoPlayer mExoplayer;
    public boolean mbAutoplay = true;
    public boolean mbIsPlaying = false;
    public boolean mbControlsActive = true;
    public int RENDERER_COUNT = 300000;
    public int mMinBufferMs = 250000;
    public final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    public final int BUFFER_SEGMENT_COUNT = 256;
    public String userAgent;
    public Uri mUri;
    public int mFileSize;

    //
    public long playbackPosition;
    public int currentWindow;
    public boolean playWhenReady;
    public AppCompatActivity mContext;

    //VARIABLES TO SET PROGRESS - COMING FROM CHAT TRIBU VIEW HOLDER
    public StringBuilder mFormatBuilder;
    public Formatter mFormatter;
    public Handler mHandler;
    public boolean mIsNotFirstPlay;
    public int currentSeekBarPosition;
    public Handler mHandlerTouch;
    public int mTimeFinished;
    public Handler mHandlerFinished;
    public BandwidthMeter mBandwidthMeter;

    //TO IMPLEMENT LEAN BACK
    public View mDecorView;
    public View mMainView;


    public ShowVideoTalkerView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_show_video_talker, this);

        ButterKnife.bind(this);

        mTalkerKey = activity.getIntent().getExtras().getString(CONTACT_ID);
        mMessageReference = activity.getIntent().getStringExtra(MESSAGE_REFERENCE);
        mUri = activity.getIntent().getData();

        //TO DISPLAY VIDEO
        mComponentListenter = new ComponentListener();
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //NEW CODE TO IMPLEMENT LEAN BACK
        mDecorView = activity.getWindow().getDecorView();

    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
