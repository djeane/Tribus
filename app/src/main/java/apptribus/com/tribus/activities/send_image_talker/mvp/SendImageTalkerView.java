package apptribus.com.tribus.activities.send_image_talker.mvp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

import apptribus.com.tribus.R;
import apptribus.com.tribus.activities.show_image.zoom_image.ZoomableDraweeView;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static apptribus.com.tribus.util.Constantes.CONTACT_ID;

/**
 * Created by User on 8/16/2017.
 */

@SuppressLint("ViewConstructor")
public class SendImageTalkerView extends FrameLayout {

    @BindView(R.id.parent)
    RelativeLayout mRelativeParent;

    @BindView(R.id.sd_image)
    ZoomableDraweeView mSimpleDraweeViewShowImage;

    @BindView(R.id.root)
    RelativeLayout mRelativeRoot;

    @BindView(R.id.appbar)
    AppBarLayout mAppBar;

    @BindView(R.id.toolbar_show_image_talker)
    Toolbar mToolbarShowImage;

    @BindView(R.id.arrow_back)
    ImageView mBtnArrowBack;

    @BindView(R.id.circle_talker_image)
    SimpleDraweeView mCircleTalkerImage;

    @BindView(R.id.tv_name_talker)
    TextView mTalkersName;

    @BindView(R.id.tv_username_talker)
    TextView mTalkerUsername;

    @BindView(R.id.et_description)
    EditText mEtDescription;

    @BindView(R.id.btn_send_message)
    FloatingActionButton mBtnSendMessage;

    public AppCompatActivity mContext;

    public String mTalkerKey;
    public Uri mUri;
    public int mFileSize;
    public String mCameFrom;


    public SendImageTalkerView(@NonNull AppCompatActivity activity) {
        super(activity);
        inflate(activity, R.layout.activity_send_image_talker, this);
        mContext = activity;
        ButterKnife.bind(this);

        mTalkerKey = activity.getIntent().getExtras().getString(CONTACT_ID);
        mUri = activity.getIntent().getData();
        mFileSize = activity.getIntent().getExtras().getInt("image_size");
        mCameFrom = activity.getIntent().getStringExtra("cameFrom");


        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    public Observable<Void> observeBtnSendImage(){
        return RxView.clicks(mBtnSendMessage);
    }

    public Observable<Void> observeBtnArrowBack(){
        return RxView.clicks(mBtnArrowBack);
    }
}
