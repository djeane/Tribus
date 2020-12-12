package apptribus.com.tribus.activities.send_video.mvp;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
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

import static apptribus.com.tribus.util.Constantes.TOPIC_KEY;
import static apptribus.com.tribus.util.Constantes.TRIBU_UNIQUE_NAME;

/**
 * Created by User on 7/12/2017.
 */

public class SendVideoView extends FrameLayout{

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

    @BindView(R.id.toolbar_show_video)
    Toolbar mToolbarShowVideo;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.circle_tribu_image)
    SimpleDraweeView mCircleTribuImage;

    @BindView(R.id.tv_name_tribu)
    TextView mTribuName;

    @BindView(R.id.tv_unique_name)
    TextView mTribuUniqueName;

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

    @BindView(R.id.et_description)
    EditText mEtDescription;

    @BindView(R.id.card_buttons)
    CardView mCardButtons;

    @BindView(R.id.btn_send_message)
    FloatingActionButton mBtnSendMessage;

    public String mTribuKey;
    public String mTopicKey;
    public String mCameFrom;


    //ANOTHER IMPLMENTATION - https://github.com/Foso/ExoPlayer-with-MediaControls/blob/master/Exoplayer/app/src/main/java/jensklingenberg/de/exampleexoplayer/MainActivity.java
    public static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    public ComponentListener mComponentListenter;
    public SimpleExoPlayer mExoplayer;
    public boolean mbAutoplay = false;
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

    public AppCompatActivity mContext;

    //CONSTRUCTOR
    public SendVideoView(@NonNull AppCompatActivity activity) {
        super(activity);
        mContext = activity;
        inflate(activity, R.layout.activity_send_video, this);
        ButterKnife.bind(this);

        //mTribuUniqueName = activity.getIntent().getExtras().getString(TRIBU_UNIQUE_NAME);
        mTribuKey = activity.getIntent().getStringExtra(TRIBU_UNIQUE_NAME);
        mTopicKey = activity.getIntent().getStringExtra(TOPIC_KEY);
        mUri = activity.getIntent().getData();
        mFileSize = activity.getIntent().getExtras().getInt("video_size");
        mCameFrom = activity.getIntent().getStringExtra("cameFrom");

        //TO DISPLAY VIDEO
        mComponentListenter = new ComponentListener();
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public Observable<Void> observeEtDescription(){
        return RxView.clicks(mEtDescription);
    }

    public Observable<Void> observeBtnSendVideo(){
        return RxView.clicks(mBtnSendMessage);
    }

    public Observable<Void> observableBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }

}
